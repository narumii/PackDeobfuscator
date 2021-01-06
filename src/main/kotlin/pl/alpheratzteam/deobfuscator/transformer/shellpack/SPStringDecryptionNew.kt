package pl.alpheratzteam.deobfuscator.transformer.shellpack

import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import pl.alpheratzteam.deobfuscator.transformer.shellpack.util.StringDecryptionUtil

class SPStringDecryptionNew : Transformer {

    override fun transform(deobfuscator: Deobfuscator) {
        val stringMap = mutableMapOf<String, String>()
        var stringBaseClass = ""
        var found = false
        var index = 0

        deobfuscator.classes.values.forEach { classNode ->
            if (!found) {
                val fieldNode = classNode.fields.stream().filter { it.name == "drong" }.findFirst()
                fieldNode.ifPresent {
                    stringBaseClass = classNode.name.substring(0, 3)
                    found = true
                }
            }

            val lowerClassName = classNode.name.toLowerCase()
            if (!lowerClassName.startsWith("i") || !lowerClassName.endsWith("i") || lowerClassName.length <= 3)
                return@forEach

            val decryptMethodNode =
                    classNode.methods.singleOrNull { it.name.toLowerCase().startsWith("i") || it.name.toLowerCase().endsWith("i") }
                            ?: return@forEach

            var size = 0
            decryptMethodNode.instructions.toArray()
                    .filter { it is MethodInsnNode && it.owner == "sun/misc/SharedSecrets" && it.desc == "()Lsun/misc/JavaLangAccess;" }
                    .filter { it.next is LdcInsnNode }
                    .forEach {
                        val ldcInsnNode = it.next as LdcInsnNode
                        size = deobfuscator.getClassSize(deobfuscator.classes[ldcInsnNode.cst.toString().replaceFirst("L", "").replace(";", "")])
                    }
            if (size == 0)
                return@forEach

            classNode.methods
                    .filter { it.name != decryptMethodNode.name }
                    .forEach { methodNode ->
                        methodNode.instructions.toArray()
                                .filter { it is LdcInsnNode }
                                .forEach {
                                    (it as LdcInsnNode).cst = StringDecryptionUtil.decrypt(it.cst.toString(), size)
                                    if (it.previous is FieldInsnNode) {
                                        stringMap[(it.previous as FieldInsnNode).name] = it.cst.toString()
                                        methodNode.instructions.remove(it.previous.previous)
                                        ++index
                                    }
                                }
                    }

            classNode.methods.remove(decryptMethodNode)
        }

        println("String Base Class: $stringBaseClass")
        println("Decrypted $index strings!")
        // TODO: 04.01.2021 change drong[number] to string using virtualizer? XD?
    }
}
