package pl.alpheratzteam.deobfuscator.transformer.shellpack

import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer

/**
 * @author Unix
 * @since 04.01.2021
 */

class SPIllegalClassRemover : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        var index = 0
        deobfuscator.classes.values
                .filter { it.name.contains("?") }
                .forEach {
                    deobfuscator.classes.remove(it.name)
                    ++index
                }
        println("Removed $index illegal classes!")
    }
}