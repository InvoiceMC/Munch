package me.outspending.munch

import kotlin.reflect.KClass

object Functions {
    fun <T : Any> KClass<T>.toMunch(): Munch<T> = Munch(this)

    inline fun runIf(condition: Boolean, block: () -> Unit) {
        if (condition) block()
    }

    inline fun <reified T> runAsync(crossinline block: () -> T): T? {
        return me.outspending.munch.Functions.runAsyncIf(true, block)
    }

    inline fun <reified T> runAsyncIf(condition: Boolean, crossinline block: () -> T): T? {
        return if (condition) {
            var result: T? = null

            val thread = Thread { result = block() }
            thread.start()

            thread.join()

            result
        } else {
            block()
        }
    }
}
