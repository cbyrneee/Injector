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

package example

import dev.dreamhopping.injector.Injector
import dev.dreamhopping.injector.clazz.loader.InjectorClassLoader
import dev.dreamhopping.injector.clazz.transformer.impl.InjectorClassTransformer
import dev.dreamhopping.injector.position.InjectPosition

/**
 * Called from EntryPoint.kt
 */
class InjectorExample {
    fun run() {
        val classLoader = Thread.currentThread().contextClassLoader as InjectorClassLoader
        classLoader.addTransformer(InjectorClassTransformer())

        Injector.inject("example/TargetClass", "print", InjectPosition.BEFORE_ALL) {
            println("wow before all...")
        }

        // You can format it using "/" or "."!
        Injector.inject("example.TargetClass", "print", InjectPosition.BEFORE_RETURN) {
            println("wow before return...")
        }

        Injector.inject("example/TargetClass", "print", InjectPosition.BEFORE_RETURN) {
            println("you can have multiple at one position...")
        }

        TargetClass().print()
    }
}