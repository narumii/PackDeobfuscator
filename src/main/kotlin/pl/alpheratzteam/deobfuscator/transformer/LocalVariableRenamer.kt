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
            .forEach { it.name = getName(it.desc) + ++index }

        // TODO: 05.01.2021 get name by desc? e.g. Ljava/lang/String; = string1
        // Zrobilem to za ciebie :thumb:
    }

    fun getName(string: String) : String {
        return if (string.contains("/")) {
            val split = string.split("/")
            split[split.size].replace(";", "")
        }else "var_"
    }
}