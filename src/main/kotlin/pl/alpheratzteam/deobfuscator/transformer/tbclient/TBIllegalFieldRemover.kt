package pl.alpheratzteam.deobfuscator.transformer.tbclient

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
        deobfuscator.getClassesAsCollection().forEach { classNode ->
            classNode.fields.removeIf { it.name.toCharArray()[0].toInt() <= 127 }

            classNode.methods.forEach { methodNode ->
                methodNode.instructions.toArray()
                        .filter { it is FieldInsnNode && it.name.toCharArray()[0].toInt() > 127 }
                        .filter { it.next.opcode == Opcodes.IFEQ }
                        .filter { it.next.next.opcode == Opcodes.ACONST_NULL }
                        .filter { it.next.next.next.opcode == Opcodes.ATHROW }
                        .forEach {
                            val ifeq = it.next
                            val aconst_null = ifeq.next
                            val athrow = aconst_null.next

                            with(methodNode.instructions) {
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