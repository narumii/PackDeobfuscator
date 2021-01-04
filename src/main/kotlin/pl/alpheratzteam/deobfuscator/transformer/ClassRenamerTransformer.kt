package pl.alpheratzteam.deobfuscator.transformer

import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import org.objectweb.asm.tree.ClassNode
import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import java.io.BufferedReader
import java.io.FileReader

/**
 * @author Unix
 * @since 04.01.2021
 */

class ClassRenamerTransformer : Transformer {

    override fun transform(deobfuscator: Deobfuscator) {
        val classMappings = mutableMapOf<String, String>()
        val mappings = mutableMapOf<String, String>()
        val bufferedReader = BufferedReader(FileReader("deobfuscator/shellpack-mappings.srg"))
        bufferedReader.lines().forEach {
            if (!it.startsWith("CL:")) {
                return@forEach
            }

            val split = it.replaceFirst("CL: ", "").split(" ")
            val fakeName = split[0]
            val originalName = split[1]
            classMappings[fakeName] = originalName
        }

        deobfuscator.classes.forEach {
            val classNode = it.value

            var className = classNode.name
            do {
                if (classMappings.containsKey(classNode.name)) {
                    className = classMappings.get(classNode.name)
                }
            } while (mappings.containsValue(className))

            mappings.put(classNode.name, className)
        }

        val classNodeMap = mutableMapOf<String, ClassNode>()
        val remapper = SimpleRemapper(mappings)
        deobfuscator.classes.forEach {
            val classNode = it.value
            val copy = ClassNode()
            classNode.accept(ClassRemapper(copy, remapper))
            classNodeMap.put(copy.name, copy)
        }

        println("Remapped ${classMappings.size} classes!")
        deobfuscator.classes.clear()
        deobfuscator.classes.putAll(classNodeMap)
        classMappings.clear()
        mappings.clear()
    }
}