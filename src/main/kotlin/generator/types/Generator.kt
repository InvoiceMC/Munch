package me.outspending.generator.types

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
}
