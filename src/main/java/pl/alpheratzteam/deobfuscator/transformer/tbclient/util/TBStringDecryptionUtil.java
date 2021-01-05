package pl.alpheratzteam.deobfuscator.transformer.tbclient.util;

/**
 * @author Unix
 * @since 03.01.2021
 */

public final class TBStringDecryptionUtil {

    private static final char[] CHARS_1 = {18482, 9093, 9094, 38931, 37157, 17794, 2323, 13346, 2131, 1828};
    private static final char[] CHARS_2 = {18464, 33795, 34643, 14338, 14400, 14484, 34617, 4152, 33540, 13107};

    private TBStringDecryptionUtil() {
    }

    public static String decode(String string) {
        try {
            char[] charArray = string.toCharArray();

            char[] cArr = new char[charArray.length];
            for (int i = 0; i < charArray.length; i++) {
                cArr[i] = (char) (charArray[i] ^ CHARS_1[i % CHARS_1.length]);
            }

            char[] cArr4 = new char[cArr.length];
            for (int j = 0; j < charArray.length; j++) {
                cArr4[j] = (char) (cArr[j] ^ CHARS_2[j % CHARS_2.length]);
            }
            return new String(cArr4);
        } catch (Exception e) {
            return string;
        }
    }
}