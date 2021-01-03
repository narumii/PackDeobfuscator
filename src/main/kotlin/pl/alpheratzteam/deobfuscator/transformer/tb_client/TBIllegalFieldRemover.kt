package pl.alpheratzteam.deobfuscator.transformer.tb_client

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FieldInsnNode
import org.objectweb.asm.tree.FieldNode
import org.objectweb.asm.tree.JumpInsnNode
import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer

/**
 * @author Unix
 * @since 03.01.2021
 */

class TBIllegalFieldRemover : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        var index = 0
        deobfuscator.classes.forEach {
            val classNode = it.value
            val fields = mutableListOf<FieldNode>()
            classNode.fields.forEach {
                if (it.name.toCharArray()[0].toInt() <= 127) {
                    return@forEach
                }

                fields.add(it)
                ++index
            }

            fields.forEach { classNode.fields.remove(it) }

            classNode.methods.forEach {
                val methodNode = it
                methodNode.instructions.forEach {
                    if (it !is FieldInsnNode) {
                        return@forEach
                    }

                    if (it.name.toCharArray()[0].toInt() <= 127) {
                        return@forEach
                    }

                    val ifeq = it.next
                    if (ifeq !is JumpInsnNode) { // ifeq?
                        return@forEach
                    }

                    if (ifeq.opcode != Opcodes.IFEQ) { // ifeq?
                        return@forEach
                    }

                    val aconst_null = ifeq.next
                    if (aconst_null.opcode != Opcodes.ACONST_NULL) { // aconst_null?
                        return@forEach
                    }

                    val athrow = aconst_null.next
                    if (athrow.opcode != Opcodes.ATHROW) { // athrow?
                        return@forEach
                    }

                    with (methodNode.instructions) {
                        remove(it) // getstatic
                        remove(ifeq) // ifeq
                        remove(aconst_null) // aconst_null
                        remove(athrow) // athrow
                    }

                    ++index
                }
            }
        }

        println("Removed $index illegal fields!")
    }
}