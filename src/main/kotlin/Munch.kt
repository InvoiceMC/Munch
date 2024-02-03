package me.outspending

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

interface Munch<T : Any> {
    companion object {
        fun <T : Any> create(clazz: KClass<T>): Munch<T> {
            return MunchImpl(clazz)
        }
    }

    fun process(): MunchClass<T>
    fun isSQLType(): Boolean
    fun getClass(): KClass<T>
    fun getProperties(): Collection<KProperty1<out T, *>>
}