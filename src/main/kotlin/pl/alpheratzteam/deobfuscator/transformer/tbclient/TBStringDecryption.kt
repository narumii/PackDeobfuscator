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
        //Zrobiles metode to jej kurwa uzywaj przyjebie
        deobfuscator.getClassesAsCollection()
                .flatMap { it.methods }
                .forEach { methodNode ->
                    methodNode.instructions
                            .filter { it is MethodInsnNode && it.owner == "qProtect" && it.name == "decode" }
                            .filter { it.previous is LdcInsnNode }
                            .forEach {
                                val ldcInsnNode = it.previous as LdcInsnNode
                                methodNode.instructions.remove(it)
                                ldcInsnNode.cst = TBStringDecryptionUtil.decode(ldcInsnNode.cst.toString())
                                ++index
                            }
                }

        println("Decrypted $index strings!")
    }
}