package me.outspending.generator.types

import me.outspending.Column
import me.outspending.MunchClass
import kotlin.reflect.KProperty1

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