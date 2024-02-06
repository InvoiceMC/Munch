package me.outspending.munch.generator

import me.outspending.munch.AllGenerator
import me.outspending.munch.Column
import me.outspending.munch.MunchClass
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * This extension function is used to generate the SQL for the data class.
 *
 * @author Outspending
 * @since 1.0.0
 */
fun <T : Any, K : Any> MunchClass<T, K>.generateTable(): String =
    generateCustom(TableGenerator(this))

class TableGenerator<T : Any, K : Any>(clazz: MunchClass<T, K>) : AllGenerator<T> {
    private val builder = StringBuilder("CREATE TABLE IF NOT EXISTS ${clazz.clazz.simpleName} (")

    private val primaryKey = clazz.primaryKey
    private val columns = clazz.columns

    override fun generate(): String {
        handlePrimaryKey()
        handleColumns()

        builder.append(");")

        return builder.toString()
    }

    override fun handlePrimaryKey() {
        val property = primaryKey.first
        val primaryKey = primaryKey.second

        val hasAutoIncrement = primaryKey.autoIncrement
        require(!(hasAutoIncrement && property.returnType.classifier != Int::class)) {
            "Auto increment can only be used on an int type!"
        }

        val type = getType(property)
        builder.append(
            "${property.name} $type PRIMARY KEY${if (hasAutoIncrement) " AUTOINCREMENT" else ""}"
        )
    }

    override fun handleColumns() {
        columns.forEach { (property, column) -> handleColumn(property, column) }
    }

    override fun handleColumn(property: KProperty1<out T, *>, column: Column) {
        val type = getType(property)
        val constraints = column.constraints

        builder.apply {
            append(", ${property.name} $type")
            constraints.forEach { constraint -> append(" ${constraint.value}") }
        }
    }

    private fun getType(clazz: KProperty1<out T, *>): String {
        return when (clazz.returnType.classifier as KClass<*>) {
            Byte::class, Int::class, Short::class -> "INTEGER"
            Double::class, Long::class, Float::class -> "REAL"
            Boolean::class -> "NUMERIC"
            else -> "TEXT"
        }
    }

}
