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
        deobfuscator.classes.values.forEach { classNode ->
            classNode.methods.forEach { methodNode ->

                //Wyjebalem checki na nulle xd wygladalo chujowo niczym ten kod unixa
                methodNode.instructions.toArray()
                        .filter { ASMUtil.isIntInsn(it) }
                        .filter { ASMUtil.isIntInsn(it.next) }
                        .filter { it.next.next.opcode == Opcodes.SWAP }
                        .filter { it.next.next.next.opcode == Opcodes.DUP_X1 }
                        .filter { it.next.next.next.next.opcode == Opcodes.POP2 }
                        .filter { ASMUtil.isIntInsn(it.next.next.next.next.next) }
                        .forEach {
                            val firstNumber = ASMUtil.getIntFromInsn(it)
                            val fakeInsn = it.next
                            val swap = fakeInsn.next
                            val dup_x1 = swap.next
                            val pop2 = dup_x1.next
                            val secondNumberInsn = pop2.next

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