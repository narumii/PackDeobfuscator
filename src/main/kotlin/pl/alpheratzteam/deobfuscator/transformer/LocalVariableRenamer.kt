package pl.alpheratzteam.deobfuscator.transformer

import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import java.util.*

/**
 * @author Unix
 * @since 05.01.2021
 */

class LocalVariableRenamer : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        var index = 0
        deobfuscator.classes.flatMap { it.value.methods }
            .filter { Objects.nonNull(it.localVariables) }
            .flatMap { it.localVariables }
            .forEach { it.name = "var_" + ++index }

        // TODO: 05.01.2021 get name by desc? e.g. Ljava/lang/String; = string1
    }
}