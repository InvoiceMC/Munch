package me.outspending.munch

import me.outspending.munch.generator.*
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
class MunchClass<K : Any, V : Any>(
    val clazz: KClass<K>,
    private val tableName: String,
    val primaryKey: Pair<KProperty1<out K, *>, PrimaryKey>,
    val columns: Map<KProperty1<out K, *>, Column>
) {
    val tableSQL: String by lazy { this.generateTable() }

    val selectAllSQL: String by lazy { this.generateSelectAll() }
    val selectSQL: String by lazy { this.generateSelect() }

    val updateSQL: String by lazy { this.generateUpdate() }
    val insertSQL: String by lazy { this.generateInsert() }

    val deleteTableSQL: String by lazy { this.generateDeleteTable() }
    val deleteAllSQL: String by lazy { this.generateDeleteAll() }
    val deleteSQL: String by lazy { this.generateDelete() }

    /**
     * This method is used to execute a custom generator. This is also used for all the main
     * generators. This method is used to generate the SQL for the data class.
     *
     * @author Outspending
     * @since 1.0.0
     */
    fun generateCustom(generator: Generator<K>): String = generator.generate()

    /**
     * This method is used to get the name of the data class.
     *
     * @return The name of the data class.
     * @author Outspending
     * @since 1.0.0
     */
    fun getName() = tableName

    /**
     * This method is used to get the number of values in the data class.
     *
     * @return The number of columns in the data class.
     * @author Outspending
     * @since 1.0.0
     */
    fun getAmount(): Int = columns.size + 1
}
