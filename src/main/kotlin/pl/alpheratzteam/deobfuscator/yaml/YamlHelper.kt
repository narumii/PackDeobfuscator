package pl.alpheratzteam.deobfuscator.yaml

import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream

/**
 * @author Unix
 * @since 04.01.2021
 */

//Konwerter be like
class YamlHelper(file: String) {

    companion object {
        private val yaml = Yaml()
    }

    private val objs = mutableMapOf<String, Any>()

    init {
        objs.putAll(yaml.load(FileInputStream(file)))
    }

    fun getInt(name: String) = objs[name] as Int

    fun getString(name: String) = if (objs.containsKey(name)) objs[name] as String else null

    fun getStringList(name: String) = objs[name] as List<String>
}