package pl.alpheratzteam.deobfuscator.transformer.shellpack.util;

/**
 * @author Unix
 * @since 04.01.2021
 */

public final class StringDecryptionUtil {

    private StringDecryptionUtil() {
    }

    public static String decrypt(String string, String stringsPoolClassName, int size) {
        final char[] chars = string.toCharArray();

        int i = 0;
        for (char c : chars) {
            c ^= size;
            c ^= stringsPoolClassName.replace("/", ".").hashCode();
            c ^= stringsPoolClassName.replace("/", ".").hashCode();
            chars[i] = c;
            i++;
        }

        return new String(chars);
    }
}