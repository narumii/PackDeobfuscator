package pl.alpheratzteam.deobfuscator.util

import java.lang.StringBuilder
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.concurrent.ThreadLocalRandom

/**
 * @author Unix
 * @since 17.12.2020
 */

object StringUtil {

    private val chars = "abcdefghijklmnopqrstuvwxyz1234567890".toCharArray()
    private val javaKeywords = mutableListOf(
            "abstract", "boolean", "break", "byte", "case", "catch", "char", "class",
            "continue", "default", "do", "while", "for", "if",
            "\u0000", "\u0001", "\u0002", "\u0004",
            "double", "else", "new", "null", "return", "short",
            "super", "static", "switch", "try", "void",
            "while", "volatile"
    )

    // generate custom string
    fun generateString(length: Int) = IntStream.range(0, length)
            .mapToObj {
                Character.toString(
                        chars[RandomUtil.int(chars.size)]
                )
            }.collect(Collectors.joining())

    // generate unreadable string - Radon be like
    fun makeUnreadable(string: String): String {
        val stringBuilder = StringBuilder()
        string.toCharArray().forEach { stringBuilder.append((it + '\u7159'.toInt())) }
        return stringBuilder.toString()
    }

    fun getStringWithJavaKeywords(length: Int): String {
        val stringBuilder = StringBuilder()
        for (index in 0..length) {
            val keyword = javaKeywords[ThreadLocalRandom.current().nextInt(0, javaKeywords.size)]

            when {
                index != 24 -> {
                    stringBuilder.append(keyword).append(" ")
                }
                else -> {
                    stringBuilder.append(keyword)
                }
            }
        }

        return stringBuilder.toString()
    }
}