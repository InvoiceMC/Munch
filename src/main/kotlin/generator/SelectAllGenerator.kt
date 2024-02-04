package me.outspending.generator

import me.outspending.MunchClass
import me.outspending.generator.types.Generator

/**
 * This class is used to generate the SQL for the data class.
 *
 * @param T The data class to generate the SQL for.
 * @since 1.0.0
 */
fun <T : Any> MunchClass<T>.generateSelectAll(): String = generateCustom(SelectAllGenerator(this))

class SelectAllGenerator<T : Any>(val clazz: MunchClass<T>) : Generator<T> {
    override fun generate(): String = "SELECT * FROM ${clazz.getName()};"
}
