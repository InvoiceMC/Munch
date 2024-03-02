package me.outspending.munch

import kotlin.reflect.KClass

fun <T : Any> KClass<T>.toMunch(): Munch<T> = Munch.create(this)
