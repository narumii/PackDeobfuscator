package pl.alpheratzteam.deobfuscator.transformer

import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer

/**
 * @author Unix
 * @since 04.01.2021
 */

class IllegalDataRemover : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        deobfuscator.getClassesAsCollection().forEach {
            it.signature = null
            it.sourceFile = null
        }
    }
}