package me.outspending.munch

import kotlin.reflect.KClass

fun interface ValueType {
    fun getSupportedTypes(): List<KClass<*>>
}