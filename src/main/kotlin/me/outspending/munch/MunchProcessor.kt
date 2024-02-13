package me.outspending.munch

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * This class is used to process all the annotations for a data class that has the [Table]
 * annotation.
 *
 * This class is used to return a [MunchClass] instance that contains the primary key and columns of
 * the data class. Then will be passed off to the [Generator] to generate the SQL.
 *
 * @param T The data class that has the [Table] annotation.
 * @constructor Creates a new [MunchProcessor] instance.
 * @property munch The [Munch] instance to be used.
 * @see MunchClass
 * @see Generator
 * @author Outspending
 * @since 1.0
 */
class MunchProcessor<T : Any>(val munch: Munch<T>) {
    private val tableName: String
        get() = tableName()

    /**
     * This method is used to process a [Munch] instance aka a data class that has the [Table]
     * annotation.
     *
     * This method will find the primary key and columns of the data class and return a [MunchClass]
     * instance. Then it will be sent off to the [Generator] to generate the SQL.
     *
     * @return A [MunchClass] instance that contains the primary key and columns of the data class.
     * @throws IllegalArgumentException If the data class does not have the [Table] annotation.
     * @see MunchClass
     * @see Generator
     * @author Outspending
     * @since 1.0
     */
    @Suppress("UNCHECKED_CAST")
    fun <K : Any> process(): MunchClass<T, K> {
        val primaryKey = getPrimaryKey()
        val columns = getColumns()

        checkTypeConstraints(primaryKey, columns)

        val primaryKeyClass = primaryKey.first.returnType.classifier as KClass<K>
        return MunchClass(munch.getClass(), primaryKeyClass, tableName, primaryKey, columns)
    }

    /**
     * Checks all the type constraints and checks if it has any unsupported types. If it does, it
     * will throw an [IllegalArgumentException].
     *
     * @param primaryKey The primary key of the data class.
     * @param columns The columns of the data class.
     * @throws IllegalArgumentException If the type does not match the value type.
     * @see PrimaryKey
     * @see Column
     * @see ColumnType
     * @author Outspending
     */
    private fun checkTypeConstraints(
        primaryKey: Pair<KProperty1<out T, *>, PrimaryKey>,
        columns: Map<KProperty1<out T, *>, Column>,
    ) {
        fun checkType(
            property: KProperty1<out T, *>,
            type: ColumnType,
        ): Boolean {
            val classifier = property.returnType.classifier
            return if (type != ColumnType.NONE) {
                require(type.supportedValues.contains(classifier)) {
                    "The type $classifier does not match the value type of $type."
                }

                true
            } else {
                true
            }
        }

        checkType(primaryKey.first, primaryKey.second.columnType)
        columns.forEach { (property, column) -> checkType(property, column.columnType) }
    }

    /**
     * This method is used to find the primary key.
     *
     * @return A [Pair] of the primary key and the [PrimaryKey] annotation.
     * @throws IllegalArgumentException If multiple primary keys are found.
     * @see PrimaryKey
     * @author Outspending
     * @since 1.0
     */
    private fun getPrimaryKey(): Pair<KProperty1<out T, *>, PrimaryKey> {
        val primaryKey =
            munch
                .properties
                .filter { it.annotations.any { annotation -> annotation is PrimaryKey } }
                .toList()

        require(primaryKey.isNotEmpty()) { "No primary key found!" }
        require(primaryKey.size == 1) { "Multiple primary keys found!" }

        val main = primaryKey[0]
        return (main to main.annotations.first { it is PrimaryKey } as PrimaryKey)
    }

    /**
     * This method is used to find all the properties that contains the [Column] annotation.
     *
     * @return A [Map] of the properties and the [Column] annotation.
     * @see Column
     * @author Outspending
     * @since 1.0
     */
    private fun getColumns(): Map<KProperty1<out T, *>, Column> {
        val clazz = munch.getClass()
        val fields = clazz.java.declaredFields
        val orderByID = fields.withIndex().associate { it.value.name to it.index }

        val columns =
            munch
                .properties
                .filter { it.annotations.any { annotation -> annotation is Column } }
                .sortedBy { orderByID[it.name] }

        require(columns.isNotEmpty()) { "No columns were found!" }

        return columns.associateWith {
            it.annotations.first { column -> column is Column } as Column
        }
    }

    /**
     * This method is used for getting the name of the table. This can be set using the [Table]
     * annotation. If it isn't set in the annotation it will grab the data class's simple name
     * instead.
     *
     * @see Table
     * @author Outspending
     * @since 1.0.0
     */
    private fun tableName(): String {
        val clazz = munch.getClass()
        val table = clazz.annotations.first { it is Table } as Table

        return table.tableName.ifEmpty { clazz.simpleName!! }
    }
}
