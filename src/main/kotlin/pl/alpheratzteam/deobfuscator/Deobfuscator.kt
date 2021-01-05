package pl.alpheratzteam.deobfuscator

import com.google.common.reflect.ClassPath
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import pl.alpheratzteam.deobfuscator.api.transformer.Transformer
import pl.alpheratzteam.deobfuscator.util.JarUtil
import pl.alpheratzteam.deobfuscator.yaml.YamlHelper
import java.io.File


/**
 * @author Unix
 * @since 16.12.2020
 */

class Deobfuscator {

    private val dataFolder: File = File("deobfuscator") // base folder

    var classes: MutableMap<String, ClassNode> = mutableMapOf() // classes from jar
    var assets: MutableMap<String, ByteArray> = mutableMapOf() // assets from jar

    fun onStart() {
        val jarFile = File(dataFolder, "jars").apply { // create files
            dataFolder.mkdir()
            mkdir()
        }

        JarUtil.loadJar(File(jarFile, "input.jar")).apply { // load classes, assets from jar
            println("Loading jar...")
        }.run {
            println("Loaded jar!")
            println("Starting transformers...")
            classes.putAll(first)
            assets.putAll(second)
        }

        val yamlHelper = YamlHelper("config.yml")
        val transformers = mutableListOf<Transformer>() // modifiers
        val classPath: ClassPath = ClassPath.from(Thread.currentThread().contextClassLoader)
        yamlHelper.getStringList("transformers").forEach { // load modifiers from yml
            classPath.topLevelClasses
                .stream()
                .filter { classInfo -> classInfo.simpleName == it }
                .findFirst()
                .ifPresent { classInfo ->
                    try {
                        transformers.add(classInfo.load().newInstance() as Transformer)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
        }

        transformers.forEach {
            val name = it.javaClass.simpleName
            var time = System.currentTimeMillis()
            println("Running $name transformer...")
            it.transform(this) // modify classes
            time = System.currentTimeMillis() - time
            println("Finished running $name transformer. [$time ms]")
            println("---------------------------------------")
        }

        JarUtil.saveJar(File(jarFile, "output.jar"), Pair(classes, assets)).apply { // save output
            println("Saving jar...")
        }.run {
            println("Saved jar!")
        }
    }

    fun getClassSize(classNode: ClassNode?): Int {
        val classWriter = ClassWriter(0)
        classNode?.accept(classWriter)
        return ClassReader(classWriter.toByteArray()).itemCount
    }

    fun getClassesAsCollection() : Collection<ClassNode> {
        return classes.values
    }
}