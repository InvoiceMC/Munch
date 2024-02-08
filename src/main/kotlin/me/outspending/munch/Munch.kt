package me.outspending.munch

import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

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
 * @see ColumnConstraint
 * @see MunchProcessor
 * @see MunchClass
 * @author Outspending
 * @since 1.0.0
 */
class Munch<T : Any>(private val clazz: KClass<T>) {
    private val munchProcessor: MunchProcessor<T> = MunchProcessor(this)

    companion object {
        /**
         * This method is used to create a [Munch] instance.
         *
         * @param clazz The data class to be used.
         * @return A [Munch] instance.
         * @since 1.0.0
         */
        fun <T : Any> create(clazz: KClass<T>): Munch<T> = Munch(clazz)
    }

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
     * @since 1.0.0
     */
    fun <K : Any> process(): MunchClass<T, K> {
        if (isSQLType() && clazz.isData) return munchProcessor.process()
        else
            throw IllegalArgumentException(
                "The class must be a data class and have the Table annotation"
            )
    }

    /**
     * This method checks if the class is an SQL type.
     *
     * @return True if the class is an SQL type, false otherwise.
     * @see Table
     * @author Outspending
     * @since 1.0.0
     */
    private fun isSQLType(): Boolean = clazz.java.isAnnotationPresent(Table::class.java)

    /**
     * This method is used to get the class of the data class.
     *
     * @return The class of the data class.
     * @since 1.0.0
     */
    fun getClass(): KClass<T> = clazz

    /**
     * This method is used to get the properties of the data class.
     *
     * @return A collection of the properties of the data class.
     * @since 1.0.0
     */
    fun getProperties(): Collection<KProperty1<out T, *>> = clazz.memberProperties
}
