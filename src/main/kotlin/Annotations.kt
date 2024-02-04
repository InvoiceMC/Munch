package me.outspending

import me.outspending.enums.ColumnConstraint
import me.outspending.enums.ColumnType

/**
 * This annotation is for the class that represents a table inside the database. This annotation is
 * needed to generate / connect to Munch's database.
 *
 * @author Outspending
 * @since 1.0.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
annotation class Table

/**
 * This annotation represents the primary key inside the table. <br> For the primary key to work,
 * the property must be of type Int and the class must be annotated with [Table]. else the Processor
 * WILL throw an exception.
 *
 * @author Outspending
 * @since 1.0.0
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class PrimaryKey(
    val type: ColumnType = ColumnType.INTEGER,
    val autoIncrement: Boolean = false
)

/**
 * This annotation represents the column inside the table. This is crucial for the processor to
 * work. Columns are something that stores the data inside the table. You can also use
 * [ColumnType.OTHER] if you want a custom type.
 *
 * If the custom type exists in the Serializer it will serialize / deserialize the data. On its own
 * it will just store the data as a string.
 *
 * @see ColumnType
 * @since 1.0.0
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column(
    val name: String = "",
    val type: ColumnType = ColumnType.TEXT,
    val constraints: Array<ColumnConstraint> = []
)
