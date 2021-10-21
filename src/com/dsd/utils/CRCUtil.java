package com.dsd.utils;

public class CRCUtil {

    private static char[] crc_tb = new char[]{
            '\000', 'အ', '⁂', 'っ', '䂄',
            '傥', '惆', '烧', '脈', '鄩',
            'ꅊ', '녫', '소',
            '톭', '', '', 'ሱ', 'Ȑ', '㉳', '≒',
            '劵',
            '䊔', '狷', '拖', '錹', '茘', '덻', 'ꍚ', '펽',
            '쎜',
            '', '', '③', '㑃', 'Р', 'ᐁ', '擦',
            '瓇', '䒤', '咅',
            'ꕪ', '땋', '蔨', '锉', '',
            '', '얬', '햍', '㙓', '♲',
            'ᘑ', 'ذ', '盗',
            '曶', '嚕', '䚴', '띛', 'ꝺ', '霙', '蜸',
            '',
            '', '힝', '잼', '䣄', '壥', '梆', '碧', 'ࡀ',
            'ᡡ',
            '⠂', '㠣', '짌', '?', '', '羚', '襈',
            '饩', 'ꤊ', '뤫',
            '嫵', '䫔', '窷', '檖', 'ᩱ',
            '੐', '㨳', '⨒', '?', '쯜',
            '﮿', '', '魹',
            '識', '묻', '꬚', '沦', '粇', '䳤', '峅',
            'Ⱒ',
            '㰃', 'ౠ', '᱁', '', 'ﶏ', '췬', '?', '괪',
            '봋',
            '赨', '鵉', '纗', '溶', '廕', '仴', '㸓',
            '⸲', 'ṑ', '๰',
            'ﾟ', '', '?', '쿼', '뼛',
            '꼺', '齙', '轸', '醈', '膩',
            '뇊', 'ꇫ', '턌',
            '섭', '', '', 'ႀ', '¡', 'ヂ', '⃣',
            '倄',
            '䀥', '灆', '恧', '莹', '鎘', 'ꏻ', '돚', '쌽',
            '팜',
            '', '', 'ʱ', 'ነ', '⋳', '㋒', '䈵',
            '刔', '扷', '牖',
            '뗪', 'ꗋ', '閨', '薉', '',
            '', '픬', '씍', '㓢', 'Ⓝ',
            'ᒠ', 'ҁ', '瑦',
            '摇', '吤', '䐅', 'ꟛ', '럺', '螙', '鞸',
            '',
            '', '윝', '휼', '⛓', '㛲', 'ڑ', 'ᚰ', '晗',
            '癶',
            '䘕', '嘴', '?', '쥭', '癩', '', '駈',
            '觩', '릊', 'ꦫ',
            '塄', '䡥', '砆', '栧', 'ᣀ',
            '࣡', '㢂', '⢣', '쭽', '?',
            '', 'ﬞ', '诹',
            '鯘', 'ꮻ', '뮚', '䩵', '婔', '樷', '稖',
            '૱',
            '᫐', '⪳', '㪒', 'ﴮ', '', '?', '쵍', '붪',
            '궋',
            '鷨', '跉', '簦', '氇', '層', '䱅', '㲢',
            'ⲃ', '᳠', 'ು',
            '', '＾', '콝', '?', '꾛',
            '뾺', '这', '鿸', '渗', '縶',
            '乕', '年', '⺓',
            '㺲', '໑', 'Ự'};

    public static byte[] getCRCByte(String command) {
        int crcint = caluCRC(command.getBytes());
        int crclow = crcint & 0xFF;
        int crchigh = crcint >> 8 & 0xFF;
        return new byte[]{(byte) crchigh, (byte) crclow};
    }

    public static boolean checkCRC(String resultValue) {
        boolean result = false;
        String firstValue = resultValue.substring(0, resultValue.length() - 2);
        String lastValue = resultValue.substring(resultValue.length() - 2);
        byte[] pByte = firstValue.getBytes();
        int returnV = caluCRC(pByte);
        String lastV = toHexString(lastValue);
        int reV = Integer.parseInt(lastV, 16);
        if (reV == returnV) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            short ch = (short) s.charAt(i);
            if (ch < 0)
                ch = (short) (ch + 256);
            String s4 = Integer.toHexString(ch);
            if (s4.length() < 2)
                s4 = "0" + s4;
            str = String.valueOf(str) + s4;
        }
        return str;
    }

    private static int caluCRC(byte[] pByte) {
        try {
            int len = pByte.length;
            int i = 0;
            int crc = 0;
            while (len-- != 0) {
                int da = 0xFF & (0xFF & crc >> 8) >> 4;
                crc <<= 4;
                crc ^= crc_tb[0xFF & (da ^ pByte[i] >> 4)];
                da = 0xFF & (0xFF & crc >> 8) >> 4;
                crc <<= 4;
                int temp = 0xFF & (da ^ pByte[i] & 0xF);
                crc ^= crc_tb[temp];
                i++;
            }
            int bCRCLow = 0xFF & crc;
            int bCRCHign = 0xFF & crc >> 8;
            if (bCRCLow == 40 || bCRCLow == 13 || bCRCLow == 10)
                bCRCLow++;
            if (bCRCHign == 40 || bCRCHign == 13 || bCRCHign == 10)
                bCRCHign++;
            crc = (0xFF & bCRCHign) << 8;
            crc += bCRCLow;
            return crc;
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }
}
