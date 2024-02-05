package me.outspending.generator

import me.outspending.Generator
import me.outspending.MunchClass
import me.outspending.PrimaryGenerator

/**
 * This extension function is used to generate the SQL for the data class.
 *
 * @param T The data class to generate the SQL for.
 * @return The SQL for the data class.
 * @since 1.0.0
 */
fun <T : Any, K : Any> MunchClass<T, K>.generateSelect(): String =
    generateCustom(SelectGenerator(this))

class SelectGenerator<T : Any, K : Any>(clazz: MunchClass<T, K>) :
    Generator<T>, PrimaryGenerator<T> {
    private val builder = StringBuilder("SELECT * FROM ${clazz.getName()} WHERE ")

    private val primaryKey = clazz.primaryKey

    override fun generate(): String {
        handlePrimaryKey()

        return builder.toString()
    }

    override fun handlePrimaryKey() {
        primaryKey?.let { (property, _) -> builder.append("${property.name} = ?;") }
    }
}
