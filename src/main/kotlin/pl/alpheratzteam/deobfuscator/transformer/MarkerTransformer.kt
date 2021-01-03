package pl.alpheratzteam.deobfuscator.transformer

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.MethodNode
import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import pl.alpheratzteam.deobfuscator.util.insnBuilder

/**
 * @author Unix
 * @since 18.12.2020
 */

class MarkerTransformer : Transformer {

    private val methodName = "hello"
    private val text = "your text here"

    override fun transform(deobfuscator: Deobfuscator) {
        val methodNode = makeMethod()
        deobfuscator.classes.forEach { it.value.methods.add(methodNode) }
    }

    private fun makeMethod(): MethodNode {
        val method = MethodNode()
        with(method) {
            access = ACC_PRIVATE or ACC_STATIC
            name = methodName
            desc = "()V"
            signature = null
            exceptions = null
            instructions = insnBuilder {
                getstatic("java/lang/System", "out", "Ljava/io/PrintStream;")
                ldc(text)
                invokevirtual("java/io/PrintStream", "println", "(Ljava/lang/String;)V", false)
                _return()
            }
        }
        return method
    }
}