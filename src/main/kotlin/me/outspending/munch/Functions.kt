package me.outspending.munch

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import kotlin.reflect.KClass

object Functions {
    @PublishedApi
    internal val EXECUTOR_SERVICE =
        Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2 + 1)

    fun <T : Any> KClass<T>.toMunch(): Munch<T> = Munch.create(this)

    inline fun <reified T> runAsync(noinline block: () -> T): CompletableFuture<T?> =
        runAsyncIf(true, block)

    fun <T> runAsyncIf(
        condition: Boolean,
        block: () -> T
    ): CompletableFuture<T?> {
        return if (condition) CompletableFuture.supplyAsync(block, EXECUTOR_SERVICE)
        else CompletableFuture.completedFuture(block())
    }
}
