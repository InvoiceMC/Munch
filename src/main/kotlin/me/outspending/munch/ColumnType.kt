package me.outspending.munch

import kotlin.reflect.KClass

/**
 * This class is for all supported column types, currently Munch doesn't support column types with
 * parameters. This will be worked on in the future but as of right now they doesn't work.
 *
 * If you are using [ColumnType.NONE] as a column type. It will default to the classifier's default
 * value. This is what the default value is for each classifier. This column type is the only one
 * that can be used on the serializer / deserializer.
 *
 * @property value The value of the column type.
 * @property valueType The value type of the column type.
 * @see Column
 * @see ColumnConstraint
 * @author Outspending
 * @since 1.0.0
 */
enum class ColumnType(val value: String, val supportedValues: List<KClass<*>>) {
    /**
     * This column type is for storing integers. Which is `"INT"` in SQLite. Currently, this type
     * supports:
     * - [Int]
     * - [Byte]
     * - [Short]
     * - [Long]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    INT("INT", listOf(Int::class, Byte::class, Short::class, Long::class)),
    /**
     * This column type is for storing integers. Which is `"TINYINT"` in SQLite. Currently, this
     * type supports:
     * - [Byte]
     * - [Short]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    TINYINT("TINYINT", listOf(Byte::class, Short::class)),
    /**
     * This column type is for storing integers. Which is `"SMALLINT"` in SQLite. Currently, this
     * type supports:
     * - [Int]
     * - [Byte]
     * - [Short]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    SMALLINT("SMALLINT", listOf(Int::class, Byte::class, Short::class)),
    /**
     * This column type is for storing integers. Which is `"MEDIUMINT"` in SQLite. Currently, this
     * type supports:
     * - [Int]
     * - [Byte]
     * - [Short]
     * - [Long]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    MEDIUMINT("MEDIUMINT", listOf(Int::class, Byte::class, Short::class, Long::class)),
    /**
     * This column type is for storing integers. Which is `"BIGINT"` in SQLite. Currently, this type
     * supports:
     * - [Int]
     * - [Byte]
     * - [Short]
     * - [Long]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    BIGINT("BIGINT", listOf(Int::class, Byte::class, Short::class, Long::class)),
    /**
     * This column type is for storing integers. Which is `"UNSIGNED BIG INT"` in SQLite. Currently,
     * this type supports:
     * - [Int]
     * - [Byte]
     * - [Short]
     * - [Long]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    UNSIGNED_BIG_INT(
        "UNSIGNED BIG INT",
        listOf(Int::class, Byte::class, Short::class, Long::class)
    ),
    /**
     * This column type is for storing integers. Which is `"INT2"` in SQLite. Currently, this type
     * supports:
     * - [Int]
     * - [Byte]
     * - [Short]
     * - [Long]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    INT2("INT2", listOf(Int::class, Byte::class, Short::class, Long::class)),
    /**
     * This column type is for storing integers. Which is `"INT8"` in SQLite. Currently, this type
     * supports:
     * - [Int]
     * - [Byte]
     * - [Short]
     * - [Long]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    INT8("INT8", listOf(Int::class, Byte::class, Short::class, Long::class)),
    /**
     * This column type is for storing strings. Which is `"TEXT"` in SQLite. Currently, this type
     * supports:
     * - [CharSequence] (String, StringBuilder, ect.)
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    TEXT("TEXT", listOf(CharSequence::class)),
    /**
     * This column type is for storing strings. Which is "CLOB" in SQLite. Currently, this type
     * supports:
     * - [CharSequence] (String, StringBuilder, ect.)
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    CLOB("CLOB", listOf(CharSequence::class)),
    /**
     * This column type is for storing byte arrays. Which is "BLOB" in SQLite. Currently, this type
     * supports:
     * - [ByteArray]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    BLOB("BLOB", listOf(ByteArray::class)),
    /**
     * This column type is for storing decimal numbers. Which is "REAL" in SQLite. Currently, this type
     * supports:
     * - [Float]
     * - [Double]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    REAL("REAL", listOf(Float::class, Double::class)),
    /**
     * This column type is for storing decimal numbers. Which is "DOUBLE" in SQLite. Currently, this type
     * supports:
     * - [Float]
     * - [Double]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    DOUBLE("DOUBLE", listOf(Float::class, Double::class)),
    /**
     * This column type is for storing decimal numbers. Which is "DOUBLE PRECISION" in SQLite. Currently,
     * this type supports:
     * - [Float]
     * - [Double]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    DOUBLE_PRECISION("DOUBLE PRECISION", listOf(Float::class, Double::class)),
    /**
     * This column type is for storing decimal numbers. Which is "FLOAT" in SQLite. Currently, this type
     * supports:
     * - [Float]
     * - [Double]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    FLOAT("FLOAT", listOf(Float::class, Double::class)),
    /**
     * This column type is for storing decimal numbers. Which is "NUMERIC" in SQLite. Currently, this type
     * supports:
     * - [Float]
     * - [Double]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    NUMERIC("NUMERIC", listOf(Float::class, Double::class)),
    /**
     * This column type is for storing booleans. Which is "BOOLEAN" in SQLite. Currently, this type
     * supports:
     * - [Boolean]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    @Deprecated("Has been removed to its own type, no longer need to call this")
    BOOLEAN("BOOLEAN", listOf(Boolean::class)),
    /**
     * This column type is for storing dates. Which is "DATE" in SQLite. Currently, this type
     * supports:
     * - [java.util.Date]
     * - [java.sql.Date]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    DATE("DATE", listOf(java.util.Date::class, java.sql.Date::class)),
    /**
     * This column type is for storing dates. Which is "DATETIME" in SQLite. Currently, this type
     * supports:
     * - [java.util.Date]
     * - [java.sql.Date]
     *
     * @see Column
     * @author Outspending
     * @since 1.0.0
     */
    DATETIME("DATETIME", listOf(java.util.Date::class, java.sql.Date::class)),

    /**
     * This [ColumnType] represents a column that defaults to the classifier's default value. If you
     * don't want to use ColumnType's default value, use [NONE].
     *
     * @author Outspending
     * @since 1.0.0
     */
    NONE("NONE", listOf())
}
