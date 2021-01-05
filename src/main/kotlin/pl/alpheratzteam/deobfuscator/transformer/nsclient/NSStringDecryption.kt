package pl.alpheratzteam.deobfuscator.transformer.nsclient

import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import pl.alpheratzteam.deobfuscator.transformer.shellpack.util.StringDecryptionUtil

/**
 * @author Unix
 * @since 05.01.2021
 */

class NSStringDecryption : Transformer {

    companion object {
        private var initialized = false
    }

    override fun transform(deobfuscator: Deobfuscator) {
        var index = 0
        deobfuscator.getClassesAsCollection().forEach { classNode ->
            val decryptMethodNode =
                classNode.methods.stream().filter { !it.name.startsWith("<") }
                    .findFirst()

            decryptMethodNode.ifPresent {
                var size = 0
                it.instructions.forEach {
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

                    size = deobfuscator.getClassSize(
                        deobfuscator.classes[ldcInsnNode.cst.toString().replaceFirst("L", "").replace(";", "")]
                    )
                }

                if (size == 0) {
                    return@ifPresent
                }

                classNode.methods.forEach { methodNode ->
                    if (methodNode.name.equals(it.name)) {
                        return@forEach
                    }

                    methodNode.instructions.forEach {
                        if (it !is LdcInsnNode) {
                            return@forEach
                        }

                        it.cst = StringDecryptionUtil.decrypt(it.cst.toString(), classNode.name, size)
                        if (!initialized) {
                            val string = it.cst.toString()
                            if (string.endsWith("=")) {
                                deobfuscator.decryptKey = string
                                println("Found decrypt key: ${deobfuscator.decryptKey}")
                            }
                        }

                        val fieldInsnNode = it.previous
                            ?: // FieldInsnNode?
                            return@forEach

                        val methodInsnNode = fieldInsnNode.previous
                            ?: // MethodInsnNode?
                            return@forEach

                        if (fieldInsnNode !is FieldInsnNode) {
                            return@forEach
                        }

                        methodNode.instructions.remove(methodInsnNode)
                        ++index
                    }
                }

                classNode.methods.remove(decryptMethodNode)
            }
        }

        initialized = true
        println("Decrypted $index strings!")
    }
}