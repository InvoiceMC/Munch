package me.outspending.generator

import me.outspending.AllGenerator
import me.outspending.Column
import me.outspending.MunchClass
import kotlin.reflect.KProperty1

/**
 * This extension function is used to generate the SQL for the data class.
 *
 * @author Outspending
 * @since 1.0.0
 */
fun <T : Any, K : Any> MunchClass<T, K>.generateInsert(): String =
    generateCustom(InsertGenerator(this))

class InsertGenerator<T : Any, K : Any>(clazz: MunchClass<T, K>) : AllGenerator<T> {
    private val builder = StringBuilder("INSERT INTO ${clazz.getName()} (")

    private val primaryKey = clazz.primaryKey
    private val columns = clazz.columns

    private val size = clazz.getAmount()
    private var currentIndex = 1

    override fun generate(): String {
        handlePrimaryKey()
        handleColumns()

        with(builder) {
            append(") VALUES (")
            repeat(size) { append("?${if (it < size - 1) ", " else ""}") }
            append(");")
        }

        return builder.toString()
    }

    override fun handlePrimaryKey() {
        primaryKey?.let { (property, _) -> builder.append("${property.name}, ") }
    }

    override fun handleColumns() {
        columns?.forEach { (property, column) ->
            currentIndex++
            handleColumn(property, column)
        }
    }

    override fun handleColumn(property: KProperty1<out T, *>, column: Column) {
        builder.append("${property.name}${if (currentIndex < size) ", " else ""}")
    }
}
