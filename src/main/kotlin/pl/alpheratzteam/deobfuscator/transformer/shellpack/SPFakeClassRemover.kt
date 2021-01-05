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
        deobfuscator.getClassesAsCollection().filter { it.version == 49 || it.methods.size == 2 || it.name.length > 3 }.forEach { classes.add(it.name) }
        classes.forEach { deobfuscator.classes.remove(it) }
        println("Removed ${classes.size} fake classes!")
    }
}