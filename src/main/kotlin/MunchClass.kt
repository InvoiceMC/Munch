package me.outspending

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

/**
 * This class is the main entry point for this library. It is basically a wrapper around the
 * [MunchProcessor] class. To make your life easier, you can use the [Munch] class to process a data
 * class that has the [Table] annotation. This class is used to return a [MunchClass] instance that
 * contains the primary key and columns of the data class. Then will be passed off to the
 * [Generator] to generate the SQL.
 *
 * @param T The data class that has the [Table] annotation.
 * @constructor Creates a new [Munch] instance.
 * @property clazz The data class to be used.
 * @see MunchClass
 * @see MunchProcessor
 * @see Table
 * @see Generator
 * @see PrimaryKey
 * @see Column
 * @see MunchProcessor
 * @author Outspending
 * @since 1.0.0
 */
class MunchClass<T : Any, K : Any>(
    val clazz: KClass<T>,
    val primaryKeyClass: KClass<K>,
    val primaryKey: Pair<KProperty1<out T, *>, PrimaryKey>,
    val columns: Map<KProperty1<out T, *>, Column>
) {
    /**
     * This method is used to execute a custom generator. This is also used for all the main
     * generators. This method is used to generate the SQL for the data class.
     *
     * @author Outspending
     * @since 1.0.0
     */
    fun generateCustom(generator: Generator<T>): String = generator.generate()

    /**
     * This method is used to get the name of the data class.
     *
     * @return The name of the data class.
     * @author Outspending
     * @since 1.0.0
     */
    fun getName() = clazz.simpleName

    /**
     * This method is used to get the number of values in the data class.
     *
     * @return The number of columns in the data class.
     * @author Outspending
     * @since 1.0.0
     */
    fun getAmount(): Int = columns.size + 1
}
