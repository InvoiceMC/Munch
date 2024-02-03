package me.outspending

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

class MunchClass<T : Any>(
    private val clazz: KClass<T>,
    private val primaryKey: Pair<KProperty1<out T, *>, PrimaryKey>?,
    private val columns: Map<KProperty1<out T, *>, Column>?
) {
    fun generateTableSQL(): String {
        val string = StringBuilder("CREATE TABLE IF NOT EXISTS ")
        val tableName = clazz.simpleName ?: throw IllegalArgumentException("Class name is null")

        string.append(tableName)
        string.append(" (")

        handlePrimaryKey(string)
        handleColumns(string)

        string.append(")")
        return string.toString()
    }

    private fun handlePrimaryKey(string: StringBuilder) {
        primaryKey?.let { (property, primaryKeyOptions) ->
            val hasAutoIncrement = primaryKeyOptions.autoIncrement
            val classifier = property.returnType.classifier

            if (hasAutoIncrement && classifier != Int::class) {
                throw IllegalArgumentException("Auto increment can only be used on an int type")
            }

            val type = convertType(classifier as KClass<*>)
            with(string) {
                append("${property.name} $type")
                append(" PRIMARY KEY${if (hasAutoIncrement) " AUTOINCREMENT" else ""}")
            }

            println("DONE!")
        }
    }

    private fun handleColumns(string: StringBuilder) {
        columns?.let {
            it.forEach { (property, column) ->
                val name = column.name.ifEmpty { property.name }
                val type = convertType(property.returnType.classifier as KClass<*>)
                val constraints = column.constraints.toList()

                with(string) {
                    append(", $name $type")
                    constraints.forEach { constraint ->
                        append(" ${convertConstraint(constraint)}")
                    }
                }
            }
        }
    }


    private fun convertType(type: KClass<*>): String {
        return when (type) {
            Int::class, Short::class, Long::class -> "INTEGER"
            Double::class, Float::class -> "REAL"
            String::class -> "TEXT"
            else -> "NULL"
        }
    }

    private fun convertConstraint(constraint: ColumnConstraint): String {
        return when (constraint) {
            ColumnConstraint.NOTNULL -> "NOT NULL"
            ColumnConstraint.UNIQUE -> "UNIQUE"
            ColumnConstraint.CHECK -> "CHECK"
            ColumnConstraint.FOREIGN -> "FOREIGN KEY"
        }
    }
}