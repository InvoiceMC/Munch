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
 * @author Outspending
 * @since 1.0.0
 */
fun <T : Any> MunchClass<T>.generateTable(): String = generateCustom(TableGenerator(this))

class TableGenerator<T : Any>(clazz: MunchClass<T>) :
    Generator<T>, PrimaryGenerator<T>, ColumnGenerator<T> {
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
        primaryKey?.let { (property, primaryKey) ->
            val hasAutoIncrement = primaryKey.autoIncrement
            require(!(hasAutoIncrement && property.returnType.classifier != Int::class)) {
                "Auto increment can only be used on an int type!"
            }

            val type = primaryKey.type.value
            builder.append(
                "${property.name} $type PRIMARY KEY${if (hasAutoIncrement) " AUTOINCREMENT" else ""}"
            )
        }
    }

    override fun handleColumns() {
        columns?.forEach { (property, column) -> handleColumn(property, column) }
    }

    override fun handleColumn(property: KProperty1<out T, *>, column: Column) {
        val name = column.name.ifEmpty { property.name }
        val type = column.type.value
        val constraints = column.constraints.toList()

        builder.apply {
            append(", $name $type")
            constraints.forEach { constraint -> append(" ${constraint.value}") }
        }
    }
}
