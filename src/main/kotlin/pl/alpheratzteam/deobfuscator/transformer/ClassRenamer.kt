package pl.alpheratzteam.deobfuscator.transformer

import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import org.objectweb.asm.tree.ClassNode
import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import pl.alpheratzteam.deobfuscator.yaml.YamlHelper
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.FileReader

/**
 * @author Unix
 * @since 04.01.2021
 */

class ClassRenamer : Transformer {

    override fun transform(deobfuscator: Deobfuscator) {
        var index = 0
        val yamlHelper = YamlHelper("config.yml")
        val classMappings = mutableMapOf<String, String>()
        val mappings = mutableMapOf<String, String>()

        val mappingsFile = yamlHelper.getString("mappings")
        if (mappingsFile != null) {
            val bufferedReader = BufferedReader(FileReader(mappingsFile))
            bufferedReader.lines().forEach {
                if (!it.startsWith("CL:")) { // CL: - classes
                    return@forEach
                }

                // TODO: 05.01.2021 methods (MD:), fields (FD:)

                val split = it.replaceFirst("CL: ", "").split(" ")
                val fakeName = split[0]
                val originalName = split[1]
                classMappings[fakeName] = originalName
            }
        }

        deobfuscator.getClassesAsCollection().forEach {
            val className = (if (classMappings.containsKey(it.name)) classMappings[it.name] else "Class_" + ++index)
                ?: return@forEach

            mappings.put(it.name, className)
        }

        val classNodeMap = mutableMapOf<String, ClassNode>()
        val remapper = SimpleRemapper(mappings)
        deobfuscator.getClassesAsCollection().forEach {
            val copy = ClassNode()
            it.accept(ClassRemapper(copy, remapper))
            classNodeMap.put(copy.name, copy)
        }

        println("Remapped ${classMappings.size} classes!")
        deobfuscator.classes.clear()
        deobfuscator.classes.putAll(classNodeMap)
        classMappings.clear()
        mappings.clear()
    }
}