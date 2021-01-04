package pl.alpheratzteam.deobfuscator.transformer.tbclient

import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import pl.alpheratzteam.deobfuscator.transformer.tbclient.util.TBStringDecryptionUtil

/**
 * @author Unix
 * @since 03.01.2021
 */

class TBStringDecryption : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        var index = 0
        deobfuscator.classes.forEach {
            it.value.methods.forEach {
                val methodNode = it
                methodNode.instructions.forEach {
                    if (it !is MethodInsnNode) {
                        return@forEach
                    }

                    if (!it.owner.equals("qProtect") || !it.name.equals("decode") || !it.desc.equals("(Ljava/lang/String;)Ljava/lang/String;")) {
                        return@forEach
                    }

                    if (it.previous !is LdcInsnNode) {
                        return@forEach
                    }

                    val ldcInsnNode = it.previous as LdcInsnNode
                    methodNode.instructions.remove(it)
                    ldcInsnNode.cst = TBStringDecryptionUtil.decode(ldcInsnNode.cst.toString())
                    ++index
                }
            }
        }

        println("Decrypted $index strings!")
    }
}