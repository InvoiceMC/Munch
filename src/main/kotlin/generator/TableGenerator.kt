package me.outspending.generator

import kotlin.reflect.KProperty1
import me.outspending.Column
import me.outspending.MunchClass

/**
 * This extension function is used to generate the SQL for the data class.
 *
 * @author Outspending
 * @since 1.0.0
 */
fun <T : Any> MunchClass<T>.generateTable(): String = generateCustom(TableGenerator(this))

class TableGenerator<T : Any>(private val clazz: MunchClass<T>) : Generator<T> {
    private val builder = StringBuilder("CREATE TABLE IF NOT EXISTS ${clazz.clazz.simpleName} (")

    override fun generate(): String {
        handlePrimaryKey()
        handleColumns()
        builder.append(")")
        return builder.toString()
    }

    override fun handlePrimaryKey() {
        clazz.primaryKey?.let { (property, primaryKey) ->
            val hasAutoIncrement = primaryKey.autoIncrement

            if (hasAutoIncrement && property.returnType.classifier != Int::class) {
                throw IllegalArgumentException("Auto increment can only be used on an int type")
            }

            val type = primaryKey.type.value

            builder.apply {
                append(
                    "${property.name} $type PRIMARY KEY${if (hasAutoIncrement) " AUTOINCREMENT" else ""}"
                )
            }
        }
    }

    override fun handleColumns() {
        clazz.columns?.forEach { (property, column) -> handleColumn(property, column) }
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
