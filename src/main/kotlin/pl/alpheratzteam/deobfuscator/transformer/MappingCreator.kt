package pl.alpheratzteam.deobfuscator.transformer

import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import java.io.File
import java.io.FileWriter

/**
 * @author Unix
 * @since 04.01.2021
 */

class MappingCreator : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        val mappings = mutableMapOf<String, String>()
        deobfuscator.classes.forEach {
            val classNode = it.value
            val field = classNode.fields.stream().filter { it.name.startsWith("__OBFID") }.findFirst()
            field.ifPresent {
                mappings[classNode.name] = it.value as String
                println("Mapping: ${classNode.name} : ${it.value}")
            }
        }

        val fileWriter = FileWriter(File("mappings.txt"))
        mappings.forEach { fileWriter.write(it.key + ":" + it.value + "\n" )}
        fileWriter.close()
    }
}