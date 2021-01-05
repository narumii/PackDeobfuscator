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

    //JA PIERDOLE UMIERAM POMUSZCIE MI
    override fun transform(deobfuscator: Deobfuscator) {
        var index = 0

        deobfuscator.classes.values.forEach { classNode ->
            classNode.methods.forEach { methodNode ->
                methodNode.instructions.toArray()
                        .filter { it is MethodInsnNode && it.owner == "qProtect" && it.name == "decode" }
                        .filter { it.previous is LdcInsnNode }
                        .forEach {
                            val ldc = it.previous as LdcInsnNode
                            methodNode.instructions.remove(it)
                            ldc.cst = TBStringDecryptionUtil.decode(ldc.cst.toString())
                            ++index
                        }
            }
        }

        println("Decrypted $index strings!")
    }
}