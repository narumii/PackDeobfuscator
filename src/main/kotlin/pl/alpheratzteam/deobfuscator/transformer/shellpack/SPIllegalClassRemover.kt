package pl.alpheratzteam.deobfuscator.transformer.shellpack

import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer

/**
 * @author Unix
 * @since 04.01.2021
 */

class SPIllegalClassRemover : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        val classes = mutableListOf<String>()
        var index = 0
        deobfuscator.classes.forEach {
            val classNode = it.value
            if (!classNode.name.contains("?")) {
                return@forEach
            }

            classes.add(classNode.name)
            ++index
        }

        classes.forEach { deobfuscator.classes.remove(it) }
        println("Removed $index illegal classes!")
    }
}