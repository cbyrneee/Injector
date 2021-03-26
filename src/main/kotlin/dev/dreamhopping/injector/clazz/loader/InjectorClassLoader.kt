/*
 *     Injector is a runtime class modification library for Kotlin
 *     Copyright (C) 2021  Conor Byrne
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package dev.dreamhopping.injector.clazz.loader

import dev.dreamhopping.injector.clazz.transformer.impl.InjectorClassTransformer
import java.io.File
import java.net.URLClassLoader

class InjectorClassLoader : URLClassLoader(emptyArray(), null) {
    private val exportTransformedClass = System.getProperty("exportTransformedClass", "false").toBoolean()
    private val transformers = mutableListOf<InjectorClassTransformer>()
    private val exclusions =
        mutableListOf("java.", "kotlin.", "sun.", "javax.", "argo.", "org.objectweb.asm.", "dev.dreamhopping.injector.")

    override fun loadClass(name: String): Class<*> {
        if (exclusions.any { name.startsWith(it) }) return javaClass.classLoader.loadClass(name)

        val pathName = name.replace(".", "/")
        val resource = getResource("$pathName.class")
            ?: javaClass.classLoader.getResource("$pathName.class") ?: throw ClassNotFoundException()

        var bytes = resource.openStream().readAllBytes()
        transformers.forEach { bytes = it.transformClass(pathName, bytes) }

        // Enable this with the JVM argument "-DexportTransformedClass=true" :D
        if (transformers.isNotEmpty() && exportTransformedClass) {
            val transformedFile = File("transformed/${name}.class")
            transformedFile.parentFile.mkdirs()
            transformedFile.writeBytes(bytes)
        }

        return defineClass(name, bytes, 0, bytes.size)
    }

    fun addTransformer(transformer: InjectorClassTransformer) = transformers.add(transformer)
    fun addExclusion(exclusion: String) = exclusions.add(exclusion)
}
