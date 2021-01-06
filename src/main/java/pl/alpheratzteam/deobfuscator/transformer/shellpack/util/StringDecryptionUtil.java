package pl.alpheratzteam.deobfuscator.transformer.shellpack.util;

/**
 * @author Unix
 * @since 04.01.2021
 */

public final class StringDecryptionUtil {

    private StringDecryptionUtil() {}

    public static String decrypt(String string, int size) {
        char[] chars = string.toCharArray();
        int i = 0;
        for (char c : chars) {
            c ^= size;
            chars[i] = c;
            i++;
        }
        return new String(chars);
    }
}