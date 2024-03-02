package me.outspending.munch.generator

import me.outspending.munch.*
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
    private val builder = StringBuilder("CREATE TABLE IF NOT EXISTS ${clazz.getName()} (")

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
        val classifier = property.returnType.classifier as KClass<*>
        require(!(hasAutoIncrement && classifier != Int::class)) {
            "Auto increment can only be used on an int type!"
        }

        val type = getType(classifier)
        builder.append(
            "${property.name} $type PRIMARY KEY${if (hasAutoIncrement) " AUTOINCREMENT" else ""}"
        )
    }

    override fun handleColumns() {
        columns.forEach { (property, column) -> handleColumn(property, column) }
    }

    override fun handleColumn(property: KProperty1<out T, *>, column: Column) {
        val columnType: ColumnType = column.columnType
        val constraints: Array<ColumnConstraint> = column.constraints

        val classifier = property.returnType.classifier as KClass<*>
        appendColumns(
            property.name,
            if (columnType == ColumnType.NONE) getType(classifier) else columnType.value,
            constraints
        )
    }

    private fun appendColumns(name: String, type: String, constraints: Array<ColumnConstraint>) {
        builder.apply {
            append(", $name $type")
            constraints.forEach { constraint -> append(" ${constraint.value}") }
        }
    }

    private fun getType(classifier: KClass<*>): String {
        return when (classifier) {
            Byte::class,
            Int::class,
            Short::class,
            Long::class -> "INTEGER"
            Double::class,
            Float::class -> "REAL"
            Boolean::class -> "BOOLEAN"
            else -> "BLOB"
        }
    }
}
