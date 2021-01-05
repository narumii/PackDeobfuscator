package pl.alpheratzteam.deobfuscator.transformer

import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer

/**
 * @author Unix
 * @since 04.01.2021
 */

class IllegalDataRemover : Transformer {

    //XD?
    override fun transform(deobfuscator: Deobfuscator) {
        deobfuscator.classes.values.forEach {
            it.signature = null
            it.sourceFile = null
        }
    }
}