package com.dsd;

import com.dsd.utils.SerialHandler;
import gnu.io.PortInUseException;
import gnu.io.UnsupportedCommOperationException;

import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        System.out.println("\nInverex inverter command test util.\n");

        SerialHandler serialHandler = new SerialHandler();
        String inverterResponse = "";
        Scanner scanner;
        String userCommand = "";

        try {
            serialHandler.InitSerialPort("COM13");

            inverterResponse = serialHandler.ExcuteCommand("QPI", true);
            System.out.println(inverterResponse);

            while (!userCommand.equals("0")) {
                System.out.print("Enter Command : ");
                scanner = new Scanner(System.in);
                userCommand = scanner.nextLine();

                if (userCommand != null && !userCommand.equals("0")) {
                    inverterResponse = serialHandler.ExcuteCommand(userCommand, true);

                    if(inverterResponse.startsWith("(") )
                    {
                        inverterResponse = inverterResponse.substring(1,inverterResponse.length());
                        Arrays.stream(inverterResponse.split(" ")).forEach(System.out::println);
                    }
                    else
                        System.out.println(inverterResponse);
                }
            }

            serialHandler.ClosePort();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
