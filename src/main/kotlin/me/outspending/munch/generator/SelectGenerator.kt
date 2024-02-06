package me.outspending.munch.generator

import me.outspending.munch.Generator
import me.outspending.munch.MunchClass
import me.outspending.munch.PrimaryGenerator

/**
 * This extension function is used to generate the SQL for the data class.
 *
 * @param T The data class to generate the SQL for.
 * @return The SQL for the data class.
 * @since 1.0.0
 */
fun <T : Any, K : Any> MunchClass<T, K>.generateSelect(): String =
    generateCustom(SelectGenerator(this))

/**
 * This class is used to generate the SQL for the data class.
 *
 * @param T The data class to generate the SQL for.
 * @since 1.0.0
 */
fun <T : Any, K : Any> MunchClass<T, K>.generateSelectAll(): String =
    generateCustom(SelectAllGenerator(this))

class SelectGenerator<T : Any, K : Any>(clazz: MunchClass<T, K>) :
    Generator<T>, PrimaryGenerator<T> {
    private val builder = StringBuilder("SELECT * FROM ${clazz.getName()} WHERE ")

    private val primaryKey = clazz.primaryKey

    override fun generate(): String {
        handlePrimaryKey()

        return builder.toString()
    }

    override fun handlePrimaryKey() {
        val property = primaryKey.first

        builder.append("${property.name} = ?;")
    }
}

class SelectAllGenerator<T : Any, K : Any>(val clazz: MunchClass<T, K>) : Generator<T> {
    override fun generate(): String = "SELECT * FROM ${clazz.getName()};"
}
