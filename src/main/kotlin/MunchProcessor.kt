package me.outspending

import me.outspending.generator.types.Generator
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

        val primaryKeyClass = primaryKey?.first?.returnType?.classifier as KClass<K>
        return MunchClass(munch.getClass(), primaryKeyClass, primaryKey, columns)
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
    fun getPrimaryKey(): Pair<KProperty1<out T, *>, PrimaryKey>? {
        val primaryKey =
            munch
                .getProperties()
                .filter { it.annotations.any { annotation -> annotation is PrimaryKey } }
                .toList()

        if (primaryKey.size > 1) throw IllegalArgumentException("Multiple primary keys found")
        if (primaryKey.isEmpty()) return null

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
    fun getColumns(): Map<KProperty1<out T, *>, Column>? {
        val columns =
            munch
                .getProperties()
                .filter { it.annotations.any { annotation -> annotation is Column } }
                .toList()

        if (columns.isEmpty()) return null

        return columns.associateWith {
            it.annotations.first { column -> column is Column } as Column
        }
    }
}
