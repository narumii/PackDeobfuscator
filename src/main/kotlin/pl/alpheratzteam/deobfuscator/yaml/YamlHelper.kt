package pl.alpheratzteam.deobfuscator.yaml

import org.yaml.snakeyaml.Yaml
import java.io.FileInputStream

/**
 * @author Unix
 * @since 04.01.2021
 */

class YamlHelper(file: String) {

    companion object {
        private val yaml = Yaml()
    }

    private val objs = mutableMapOf<String, Any>()

    init {
        objs.putAll(yaml.load(FileInputStream(file)))
    }

    fun getInt(name: String) : Int {
        return objs[name] as Int
    }

    fun getString(name: String) : String {
        return objs[name] as String
    }

    fun getStringList(name: String) : List<String> {
        return objs[name] as List<String>
    }
}