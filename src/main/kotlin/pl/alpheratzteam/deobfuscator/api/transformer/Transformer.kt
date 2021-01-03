package pl.alpheratzteam.deobfuscator.api.transformer

import pl.alpheratzteam.deobfuscator.Deobfuscator

/**
 * @author Unix
 * @since 16.12.2020
 */

interface Transformer {
    fun transform(deobfuscator: Deobfuscator)
}