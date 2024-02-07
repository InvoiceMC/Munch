package me.outspending.munch

import kotlin.reflect.KClass

interface ValueType {
    fun getSupportedTypes(): List<KClass<*>>
}