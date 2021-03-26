# Injector
A side-project to learn about modifying classes at runtime using ASM. A mixin-like library, shouldn't be used in production.

It was originally meant to be for a project of mine, [PufferfishModLoader](https://github.com/PufferfishModLoader), specifically a part of [PufferfishAPI](https://github.com/PufferfishModLoader/PufferfishAPI) but I decided to make it a stand-alone project for my testing purposes.

## Using Injector
### Preparing for injecting
It is advised to change your classloader as early as possible to make sure you can inject into the classes that you wish to. 

``EntryPoint.kt``
```kt
fun main(args: Array<String>) {
    val classLoader = InjectorClassLoader()
    Thread.currentThread().contextClassLoader = classLoader

    // Example of invoking your class through the classloader
    val clazz = classLoader.loadClass("Example")
    clazz.getMethod("run").invoke(clazz.getDeclaredConstructor().newInstance())
}
```

Now, you can register the ``InjectorClassLoader`` in your class that was called from the entry point.

``Example#run``
```kt
fun run() {
    val classLoader = Thread.currentThread().contextClassLoader as InjectorClassLoader
    classLoader.addTransformer(InjectorClassTransformer())
       
    ...
}
```

### Injecting into a target class
Once you have your classloader changed, you can start using Injector!

**Normal example**
```kt
Injector.inject("dev/dreamhopping/example/Test", "print", "()V", InjectPosition.BeforeAll) {
    println("Hello World!")
}

Injector.inject("dev.dreamhopping.example.Test", "print", "()V", InjectPosition.BeforeReturn) {
    println("Goodbye World!")
}
```

**Kotlin DSL example**
```kt
injectMethod("dev/dreamhopping/example/Test", "print", "()V") {
    println("Hello World!")
}

injectMethod("dev.dreamhopping.example.Test", "print", "()V", beforeReturn) {
    println("Goodbye World!")
}
```

### Example Injector Output
When your class is modified at runtime, this is a simplified version of what it will look like in Kotlin code:
```kt
class Test {
    fun print() {
        injectorMethod0() // Injector reference
        println("Original code")
        injectorMethod1() // Injector reference
    }
    
    fun injectorMethod0() {
        println("Hello World!")
    }
    
    fun injectorMethod1() {
        println("Goodbye World!")
    }
}
```
