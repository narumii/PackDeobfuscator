package pl.alpheratzteam.deobfuscator.transformer.shellpack

import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer

/**
 * @author Unix
 * @since 04.01.2021
 */

class SPFakeClassRemover : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        var index = 0
        deobfuscator.classes.values
                .filter { it.version == 49 && it.methods.size == 2 && it.name.length >= 3 }
                .forEach {
                    deobfuscator.classes.remove(it.name)
                    ++index
                }
        println("Removed $index fake classes!")
    }
}