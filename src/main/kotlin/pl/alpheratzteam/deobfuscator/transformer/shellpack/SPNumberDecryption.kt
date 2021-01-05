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
        deobfuscator.classes.values.forEach { classNode ->
            classNode.methods.forEach { methodNode ->
                methodNode.instructions.toArray()
                        .filter { ASMUtil.isIntInsn(it) }
                        .filter { ASMUtil.isIntInsn(it.next) }
                        .filter { it.next.next.opcode != Opcodes.IAND }
                        .forEach {
                            val secondNumberInsn = it.next
                            val iand = secondNumberInsn.next
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