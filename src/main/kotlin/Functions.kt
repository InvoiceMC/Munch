package me.outspending

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.primaryConstructor

object Functions {
    fun <T : Any> KClass<T>.toMunch(): Munch<T> = Munch(this)
}