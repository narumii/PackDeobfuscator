package pl.alpheratzteam.deobfuscator.transformer.shellpack

import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer

/**
 * @author Unix
 * @since 04.01.2021
 */

class SPFakeClassRemover : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        val classes = mutableListOf<String>()
        var index = 0
        deobfuscator.classes.forEach {
            val classNode = it.value
            if (classNode.version != 49 || classNode.methods.size != 2 || classNode.name.length <= 3) {
                return@forEach
            }

            classes.add(classNode.name)
            ++index
        }

        classes.forEach { deobfuscator.classes.remove(it) }
        println("Removed $index fake classes!")
    }
}