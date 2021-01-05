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
        deobfuscator.getClassesAsCollection().filter { it.name.contains("?") }.forEach { classes.add(it.name) }
        classes.forEach { deobfuscator.classes.remove(it) }
        println("Removed ${classes.size} illegal classes!")
    }
}