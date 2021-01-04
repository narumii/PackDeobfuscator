package pl.alpheratzteam.deobfuscator.transformer.tbclient

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.LdcInsnNode
import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import pl.alpheratzteam.deobfuscator.util.ASMUtil

/**
 * @author Unix
 * @since 03.01.2021
 */

class TBNumberDecryption : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        var index = 0
        deobfuscator.classes.forEach {
            it.value.methods.forEach {
                val methodNode = it
                methodNode.instructions.forEach {
                    if (!ASMUtil.isIntInsn(it)) { // first ldc
                        return@forEach
                    }

                    val firstNumber = ASMUtil.getIntFromInsn(it)
                    val fakeInsn = it.next ?: return@forEach
                    if (!ASMUtil.isIntInsn(fakeInsn)) { // fake ldc
                        return@forEach
                    }

                    val swap = fakeInsn.next
                    if (swap.opcode != Opcodes.SWAP) { // swap
                        return@forEach
                    }

                    val dup_x1 = swap.next
                    if (dup_x1.opcode != Opcodes.DUP_X1) { // dup_x1
                        return@forEach
                    }

                    val pop2 = dup_x1.next
                    if (pop2.opcode != Opcodes.POP2) { // pop2
                        return@forEach
                    }

                    val secondNumberInsn = pop2.next
                    if (!ASMUtil.isIntInsn(secondNumberInsn)) { // second ldc
                        return@forEach
                    }

                    val secondNumber = ASMUtil.getIntFromInsn(secondNumberInsn)
                    (secondNumberInsn as LdcInsnNode).cst = firstNumber xor secondNumber
                    with (methodNode.instructions) {
                        remove(it) // first number
                        remove(fakeInsn) // fake number
                        remove(swap) // swap
                        remove(dup_x1) // dup_x1
                        remove(pop2) // pop2
                        remove(secondNumberInsn.next) // ixor
                    }

                    ++index
                }
            }
        }

        println("Decrypted $index numbers!")
    }
}