package me.outspending.generator

import me.outspending.Column
import me.outspending.MunchClass
import me.outspending.PrimaryKey
import kotlin.reflect.KProperty1

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

    /**
     * This method is to handle the [PrimaryKey] annotation. Which is passed in to the [MunchClass]
     * instance.
     *
     * If you need a reference to the primary key, you can use the [TableGenerator.handlePrimaryKey]
     * property.
     *
     * @author Outspending
     * @since 1.0.0
     */
    fun handlePrimaryKey()

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
