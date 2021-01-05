package pl.alpheratzteam.deobfuscator.transformer.nsclient

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import pl.alpheratzteam.deobfuscator.Deobfuscator
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import pl.alpheratzteam.deobfuscator.util.JarUtil
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

/**
 * @author Unix
 * @since 05.01.2021
 */

class ClassDecryptor : Transformer {
    override fun transform(deobfuscator: Deobfuscator) {
        val cipher = Cipher.getInstance("AES")
        cipher.init(Cipher.DECRYPT_MODE, SecretKeySpec(Base64.getDecoder().decode(deobfuscator.decryptKey), "AES"))

        loadJar(File(File(deobfuscator.dataFolder, "jars"), "input.jar"), cipher, deobfuscator).apply { // load classes
            println("Loading encrypt jar...")
        }.run {
            println("Loaded encrypt jar!")
            deobfuscator.classes.clear()
            deobfuscator.classes.putAll(this)
            println("Decrypted ${this.size} classes!")
        }
    }

    fun loadJar(file: File, cipher: Cipher, deobfuscator: Deobfuscator): MutableMap<String, ClassNode> {
        val classes: MutableMap<String, ClassNode> = mutableMapOf()

        JarFile(file).use {
            val entries: Enumeration<JarEntry> = it.entries()
            while (entries.hasMoreElements()) {
                val jarEntry: JarEntry = entries.nextElement()
                try {
                    val bytes: ByteArray = JarUtil.asByteArray(it.getInputStream(jarEntry))
                    if (!jarEntry.name.endsWith(".mc")) {
                        continue
                    }

                    val classNode = ClassNode()
                    val classReader = ClassReader(cipher.doFinal(bytes))
                    classReader.accept(classNode, ClassReader.EXPAND_FRAMES)
                    classes[classNode.name] = classNode
                    deobfuscator.assets.remove(jarEntry.name)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
        }

        return classes
    }
}