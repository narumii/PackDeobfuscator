package pl.alpheratzteam.deobfuscator.transformer.tbclient.util;

/**
 * @author Unix
 * @since 03.01.2021
 */

public final class TBStringDecryptionUtil {

    private TBStringDecryptionUtil() {
    }

    public static String decode(String str) {
        try {
            char[] charArray = str.toCharArray();
            char[] cArr = new char[charArray.length];
            char[] cArr2 = {18482, 9093, 9094, 38931, 37157, 17794, 2323, 13346, 2131, 1828};
            char[] cArr3 = {18464, 33795, 34643, 14338, 14400, 14484, 34617, 4152, 33540, 13107};
            for (int i = 0; i < charArray.length; i++) {
                cArr[i] = (char) (charArray[i] ^ cArr2[i % cArr2.length]);
            }
            char[] cArr4 = new char[cArr.length];
            for (int i2 = 0; i2 < charArray.length; i2++) {
                cArr4[i2] = (char) (cArr[i2] ^ cArr3[i2 % cArr3.length]);
            }
            return new String(cArr4);
        } catch (Exception e) {
            return str;
        }
    }
}