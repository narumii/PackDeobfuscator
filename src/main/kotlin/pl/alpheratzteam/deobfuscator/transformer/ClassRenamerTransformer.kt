package pl.alpheratzteam.deobfuscator.transformer

import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import org.objectweb.asm.tree.ClassNode
import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import pl.alpheratzteam.deobfuscator.yaml.YamlHelper
import java.io.BufferedReader
import java.io.FileReader

/**
 * @author Unix
 * @since 04.01.2021
 */

class ClassRenamerTransformer : Transformer {

    override fun transform(deobfuscator: Deobfuscator) {
        val yamlHelper = YamlHelper("config.yml")
        val classMappings = mutableMapOf<String, String>()
        val mappings = mutableMapOf<String, String>()
        val bufferedReader = BufferedReader(FileReader(yamlHelper.getString("mappings")))

        bufferedReader.lines().filter { it.startsWith("CL:") }
                .forEach {
                    val split = it.replaceFirst("CL: ", "").split(" ")
                    classMappings[split[0]] = split[1]
                }

        deobfuscator.classes.forEach {
            val className = classMappings.getOrDefault(it.value.name, it.value.name)
            mappings[it.value.name] = className
        }

        val classNodeMap = mutableMapOf<String, ClassNode>()
        val remapper = SimpleRemapper(mappings)
        deobfuscator.classes.forEach {
            val copy = ClassNode()
            it.value.accept(ClassRemapper(copy, remapper))
            classNodeMap[copy.name] = copy
        }

        println("Remapped ${classMappings.size} classes!")
        deobfuscator.classes.clear()
        deobfuscator.classes.putAll(classNodeMap)
        classMappings.clear()
        mappings.clear()
    }
}