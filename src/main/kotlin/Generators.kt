package me.outspending

import kotlin.reflect.KProperty1

/**
 * This class defines the interface for all generators to implement. This class is used to generate
 * the SQL for the data class.
 *
 * @param T The data class to generate the SQL for.
 * @param K The type of the primary key.
 * @constructor Create empty All generator
 * @since 1.0.0
 */
interface AllGenerator<T : Any> : Generator<T>, ColumnGenerator<T>, PrimaryGenerator<T>

/**
 * This class defines the interface for all generators to implement. This class is used to generate
 * the SQL for the data class.
 *
 * @author Outspending
 * @since 1.0.0
 */
interface Generator<T> {

    /**
     * This method is used to generate the SQL for the data class.
     *
     * @author Outspending
     * @since 1.0.0
     */
    fun generate(): String
}

/**
 * This class defines the interface for all generators to implement. This class is used to generate
 * the SQL for the data class.
 *
 * @param T The data class to generate the SQL for.
 * @constructor Create empty Primary generator
 * @since 1.0.0
 */
interface PrimaryGenerator<T : Any> {
    /**
     * This method is to handle the primary key of the data class. Which is passed in to the
     * [MunchClass] instance.
     *
     * If you need a reference to the primary key, you can use the [TableGenerator.handlePrimaryKey]
     * property.
     *
     * @author Outspending
     * @since 1.0.0
     */
    fun handlePrimaryKey()
}

/**
 * This class defines the interface for all generators to implement. This class is used to generate
 * the SQL for the data class.
 *
 * @param T The data class to generate the SQL for.
 * @constructor Create empty Column generator
 * @since 1.0.0
 */
interface ColumnGenerator<T : Any> {
    /**
     * This method is to handle the columns of the data class. Which is passed in to the
     * [MunchClass] instance.
     *
     * If you need a reference to the columns, you can use the [TableGenerator.handleColumns]
     * property.
     *
     * @author Outspending
     * @since 1.0.0
     */
    fun handleColumns()

    /**
     * This method is to handle a single column of the data class. Which is passed in to the
     * [MunchClass] instance.
     *
     * If you need a reference to the column, you can use the [TableGenerator.handleColumn]
     * property.
     *
     * @param property The property of the data class.
     * @param column The [Column] annotation of the data class.
     * @author Outspending
     * @since 1.0.0
     */
    fun handleColumn(property: KProperty1<out T, *>, column: Column)
}