package pl.alpheratzteam.deobfuscator.transformer.shellpack

import org.objectweb.asm.Opcodes
import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import pl.alpheratzteam.deobfuscator.util.ASMUtil

/**
 * @author Unix
 * @since 04.01.2021
 */

class SPNumberDecryption : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        var index = 0
        deobfuscator.classes.forEach {
            it.value.methods.forEach {
                val methodNode = it
                methodNode.instructions.forEach {
                    if (!ASMUtil.isIntInsn(it)) { // first ldc
                        return@forEach
                    }

                    val secondNumberInsn = it.next ?: return@forEach
                    if (!ASMUtil.isIntInsn(secondNumberInsn)) { // second ldc
                        return@forEach
                    }

                    val iand = secondNumberInsn.next
                    if (iand.opcode != Opcodes.IAND) { // iand
                        return@forEach
                    }

                    val firstNumber = ASMUtil.getIntFromInsn(it)
                    val secondNumber = ASMUtil.getIntFromInsn(secondNumberInsn)
                    val originalNumber = firstNumber and secondNumber

                    methodNode.instructions.insertBefore(it, ASMUtil.getIntInsn(originalNumber))
                    with(methodNode.instructions) {
                        remove(it) // first ldc
                        remove(secondNumberInsn) // second ldc
                        remove(iand) // iand
                    }
                    ++index
                }
            }
        }

        println("Decrypted $index numbers!")
    }
}