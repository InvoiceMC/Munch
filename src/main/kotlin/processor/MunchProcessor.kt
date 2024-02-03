package me.outspending.processor

import me.outspending.Munch
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

interface MunchProcessor<T> {
    companion object {
        fun <T> create(munch: Munch<T>): MunchProcessor<T> {
            return MunchProcessorImpl(munch)
        }
    }

    fun getProperty(clazz: KClass<*>): KProperty1<out Any, *>
}