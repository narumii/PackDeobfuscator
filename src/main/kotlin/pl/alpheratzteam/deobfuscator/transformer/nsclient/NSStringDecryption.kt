package pl.alpheratzteam.deobfuscator.transformer.nsclient

import org.objectweb.asm.Type
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

            decryptMethodNode.ifPresent { decryptMethod ->
                var size = 0
                decryptMethod.instructions.toArray()
                        .filter { it is MethodInsnNode && it.owner == "sun/misc/SharedSecrets" && it.desc == "()Lsun/misc/JavaLangAccess;" }
                        .filter { it.next is LdcInsnNode }
                        .forEach {
                            val ldcInsnNode = it.next as LdcInsnNode
                            size = deobfuscator.getClassSize(deobfuscator.classes[(ldcInsnNode.cst as Type).internalName])
                        }

                if (size == 0) {
                    return@ifPresent
                }

                classNode.methods
                        .filter { it.name != decryptMethod.name }
                        .forEach { methodNode ->
                            methodNode.instructions
                                    .filter { it is LdcInsnNode }
                                    .forEach {
                                        val ldcInsnNode = it.next as LdcInsnNode
                                        ldcInsnNode.cst = StringDecryptionUtil.decrypt(ldcInsnNode.cst.toString(), size)
                                        if (!initialized) {
                                            val string = ldcInsnNode.toString()
                                            if (string.endsWith("=")) {
                                                deobfuscator.decryptKey = string
                                                println("Found decrypt key: ${deobfuscator.decryptKey}")
                                            }
                                        }

                                        val fieldInsnNode = it.previous
                                        val methodInsnNode = fieldInsnNode.previous
                                        if (fieldInsnNode !is FieldInsnNode)
                                            return@forEach

                                        methodNode.instructions.remove(methodInsnNode)
                                        ++index
                                    }
                        }
                classNode.methods.remove(decryptMethod)
            }
        }

        initialized = true
        println("Decrypted $index strings!")
    }
}