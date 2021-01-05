package pl.alpheratzteam.deobfuscator.transformer.shellpack

import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import pl.alpheratzteam.deobfuscator.transformer.shellpack.util.StringDecryptionUtil

/**
 * @author Unix
 * @since 04.01.2021
 */

class SPStringDecryption : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        val stringMap = mutableMapOf<String, String>()
        var stringBaseClass = ""
        var found = false
        var index = 0
        deobfuscator.getClassesAsCollection().forEach { classNode ->
            if (!found) {
                val fieldNode = classNode.fields.stream().filter { it.name.equals("drong") }
                    .findFirst()

                fieldNode.ifPresent {
                    stringBaseClass = classNode.name.substring(0, 3)
                    found = true
                }
            }

            val lowerClassName = classNode.name.toLowerCase()
            if (!lowerClassName.startsWith("i") || !lowerClassName.endsWith("i") || lowerClassName.length <= 3) {
                return@forEach
            }

            val decryptMethodNode =
                classNode.methods.stream().filter { it.name.toLowerCase().startsWith("i") || it.name.toLowerCase().endsWith("i") }
                    .findFirst()
                    .get()

            var size = 0
            decryptMethodNode.instructions.forEach {
                if (it !is MethodInsnNode) {
                    return@forEach
                }

                if (!it.owner.equals("sun/misc/SharedSecrets") || !it.name.equals("getJavaLangAccess") || !it.desc.equals("()Lsun/misc/JavaLangAccess;")) {
                    return@forEach
                }

                val ldcInsnNode = it.next
                if (ldcInsnNode !is LdcInsnNode) {
                    return@forEach
                }

                size = deobfuscator.getClassSize(deobfuscator.classes[ldcInsnNode.cst.toString().replaceFirst("L", "").replace(";", "")])
            }

            if (size == 0) {
                return@forEach
            }

            classNode.methods.forEach {
                val methodNode = it
                if (methodNode.name.equals(decryptMethodNode.name)) {
                    return@forEach
                }

                methodNode.instructions.forEach {
                    if (it !is LdcInsnNode) {
                        return@forEach
                    }

                    it.cst = StringDecryptionUtil.decrypt(it.cst.toString(), classNode.name, size)
                    val fieldInsnNode = it.previous
                        ?: // FieldInsnNode?
                        return@forEach

                    val methodInsnNode = fieldInsnNode.previous
                        ?: // MethodInsnNode?
                        return@forEach

                    if (fieldInsnNode !is FieldInsnNode) {
                        return@forEach
                    }

                    stringMap.put(fieldInsnNode.name, it.cst.toString())
                    methodNode.instructions.remove(methodInsnNode)
                    ++index
                }
            }

            classNode.methods.remove(decryptMethodNode)
        }

        println("String Base Class: $stringBaseClass")
//        deobfuscator.classes.forEach {
//            val classNode = it.value
//            if (!classNode.name.startsWith(stringBaseClass)) {
//                return@forEach
//            }
//        }

        // TODO: 04.01.2021 change drong[number] to string using virtualizer?
        println("Decrypted $index strings!")
    }
}