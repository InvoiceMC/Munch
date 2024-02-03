package me.outspending

import kotlin.reflect.KClass

interface Munch<T> {
    companion object {
        fun <T : Any> create(clazz: KClass<T>): Munch<T> {
            return MunchImpl(clazz)
        }
    }

    fun process(): MunchClass<T>
    fun isSQLType(): Boolean
    fun hasPrimaryKey(): Boolean
    fun getClass(): KClass<T>
}