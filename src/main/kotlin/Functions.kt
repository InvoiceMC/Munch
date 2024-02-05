package me.outspending

import kotlin.reflect.KClass

object Functions {
    fun <T : Any> KClass<T>.toMunch(): Munch<T> = Munch(this)

    inline fun runIf(condition: Boolean, block: () -> Unit) {
        if (condition) block()
    }

    inline fun runAsync(crossinline block: () -> Unit) = Thread { block() }.start()
}
