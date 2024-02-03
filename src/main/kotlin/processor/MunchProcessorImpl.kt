package me.outspending.processor

import me.outspending.Munch
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMembers

class MunchProcessorImpl<T>(val munch: Munch<T>) : MunchProcessor<T> {
    private val properties = munch.getClass().

    override fun getProperty(clazz: KClass<*>): KProperty1<out Any, *> {
        TODO("Not yet implemented")
    }
}