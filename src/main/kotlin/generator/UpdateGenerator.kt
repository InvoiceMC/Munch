package me.outspending.generator

import kotlin.reflect.KProperty1
import me.outspending.Column
import me.outspending.MunchClass
import me.outspending.generator.types.ColumnGenerator
import me.outspending.generator.types.Generator
import me.outspending.generator.types.PrimaryGenerator

/**
 * This extension function is used to generate the SQL for the data class.
 *
 * @param T The data class to generate the SQL for.
 * @return The SQL for the data class.
 * @since 1.0.0
 */
fun <T : Any, K : Any> MunchClass<T, K>.generateUpdate(): String =
    generateCustom(UpdateGenerator(this))

class UpdateGenerator<T : Any, K : Any>(clazz: MunchClass<T, K>) :
    Generator<T>, PrimaryGenerator<T>, ColumnGenerator<T> {
    private val builder = StringBuilder("UPDATE ${clazz.getName()} SET ")

    private val primaryKey = clazz.primaryKey
    private val columns = clazz.columns

    private val size = clazz.getAmount()
    private var currentIndex = 1

    override fun generate(): String {
        handleColumns()

        builder.append(" WHERE ")
        handlePrimaryKey()

        return builder.toString()
    }

    override fun handlePrimaryKey() {
        primaryKey?.let { (property, _) -> builder.append("${property.name} = ?;") }
    }

    override fun handleColumns() {
        columns?.forEach { (property, column) ->
            currentIndex++
            handleColumn(property, column)
        }
    }

    override fun handleColumn(property: KProperty1<out T, *>, column: Column) {
        builder.append("${property.name} = ?${if (currentIndex < size) ", " else ""}")
    }
}
