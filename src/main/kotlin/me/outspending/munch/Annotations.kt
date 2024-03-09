package me.outspending.munch

/**
 * This annotation is for the class that represents a table inside the database. This annotation is
 * needed to generate / connect to Munch's database.
 *
 * If tableName is set it will set the name of the table, if it isn't set it will grab the parent
 * class's simpleName and use that instead.
 *
 * @property tableName The name of the table.
 * @author Outspending
 * @since 1.0.0
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Table(val tableName: String = "")

/**
 * This annotation represents the primary key inside the table. <br> For the primary key to work,
 * the property must be of type Int and the class must be annotated with [Table]. else the Processor
 * WILL throw an exception.
 *
 * @property columnType A SQLite type for the primary key. (Default: [ColumnType.NONE])
 * @property autoIncrement If the primary key should auto increment. This only works if the
 *   classifier is an Int.
 * @see ColumnType
 * @author Outspending
 * @since 1.0.0
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class PrimaryKey(
    val columnType: ColumnType = ColumnType.NONE,
    val autoIncrement: Boolean = false
)

/**
 * This annotation represents the column inside the table. This is crucial for the processor to
 * work. Columns are something that stores the data inside the table.
 *
 * If the custom type exists in the Serializer it will serialize / deserialize the data. On its own
 * it will just store the data as a string.
 *
 * @property columnType A SQLite type for the primary key. (Default: [ColumnType.NONE])
 * @property constraints Defines the SQLite constraints for the column.
 * @see ColumnType
 * @author Outspending
 * @since 1.0.0
 */
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class Column(
    val columnType: ColumnType = ColumnType.NONE,
    vararg val constraints: ColumnConstraint = []
)
