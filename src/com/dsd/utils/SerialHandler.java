package com.dsd.utils;

import gnu.io.*;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Scanner;

public class SerialHandler {

    private CommPortIdentifier commPortIdentifier;
    private InputStream input = null;
    private OutputStream output = null;
    private SerialPort serialPort;

    private Enumeration comPortList;
    private int baudRate = 2400;

    public boolean InitSerialPort(String portName) throws PortInUseException, UnsupportedCommOperationException, IOException, NoSuchPortException {

        if (portName.length() == 0) {
            comPortList = CommPortIdentifier.getPortIdentifiers();
            if (!comPortList.hasMoreElements()) {
                System.out.println("No Comm ports detected.");
                System.exit(0);
            }

            while (comPortList.hasMoreElements()) {
                commPortIdentifier = (CommPortIdentifier) comPortList.nextElement();

                if (commPortIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                    System.out.println("Found: " + commPortIdentifier.getName());

                }
            }

            System.out.println("Enter Port Number (case sensitive): ");
            Scanner scanner = new Scanner(System.in);
            String selectedPort = scanner.nextLine();

            if (selectedPort == null || selectedPort.length() == 0) {
                System.out.println("Exiting Program, as no port entered.");
                System.exit(0);
            }

            commPortIdentifier = CommPortIdentifier.getPortIdentifier(selectedPort);

        } else {
            commPortIdentifier = CommPortIdentifier.getPortIdentifier(portName);
        }

        serialPort = (SerialPort) commPortIdentifier.open("DSD", baudRate);
        serialPort.enableReceiveTimeout(1200);
        serialPort.setSerialPortParams(baudRate, 8, 1, 0);
        input = this.serialPort.getInputStream();
        output = this.serialPort.getOutputStream();
        System.out.println("Port " + serialPort.getName() + " successfully opened.\n");

        return true;
    }

    public synchronized String ExcuteCommand(String command, boolean isResponse) {
        boolean result = true;
        String returnValue = "";
        try {
            int time = 0;
            while ((returnValue == null || returnValue.length() == 0 || returnValue.startsWith("(NAK")) && time < 3) {
                clearbuffer();
                byte[] crc = CRCUtil.getCRCByte(command);
                byte[] bytes = command.getBytes();
                System.out.println("Command : " + command + " CommandHex : " + DatatypeConverter.printHexBinary(bytes) + " : CRCHex : "+ DatatypeConverter.printHexBinary(crc));
                this.output.write(bytes);
                this.output.write(crc);
                output.write(13);
                output.flush();
                if (isResponse) {
                    long end = System.currentTimeMillis() + 10000L;
                    StringBuilder sb = new StringBuilder();
                    boolean flag = false;
                    while (System.currentTimeMillis() < end) {
                        int ch;
                        if ((ch = input.read()) >= 0) {
                            if (ch != 13) {
                                sb.append((char) ch);
                                continue;
                            }
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        if (command.indexOf("P") == 0)
                            System.out.println("4567890::" + returnValue);
                        result = false;
                    }
                    returnValue = sb.toString();
                } else {
                    returnValue = null;
                    break;
                }
                if (CRCUtil.checkCRC(returnValue)) {
                    returnValue = returnValue.substring(0, returnValue.length() - 2);
                } else {
                    returnValue = "";
                }
                time++;
            }
        } catch (Exception ex) {
            result = false;
        } finally {
            //countErrorandNotifyProcesser(result);
        }
        //Debug.debug(command, returnValue);
        return returnValue;
    }

    private void clearbuffer() {
        try {
            int buflen = this.input.available();
            while (buflen > 0) {
                input.read();
                buflen--;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void ClosePort() {
        System.out.println("\nClosing port.");
        if (input != null)
            try {
                input.close();
            } catch (IOException iOException) {
            }
        if (output != null)
            try {
                output.close();
            } catch (IOException iOException) {
            }
        if (serialPort != null)
            try {
                serialPort.close();
            } catch (Exception exception) {
            }
    }
}
