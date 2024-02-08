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
    INT("INT", listOf(Int::class, Byte::class, Short::class, Long::class)),
    TINYINT("TINYINT", listOf(Byte::class, Short::class)),
    SMALLINT("SMALLINT", listOf(Int::class, Byte::class, Short::class)),
    MEDIUMINT("MEDIUMINT", listOf(Int::class, Byte::class, Short::class, Long::class)),
    BIGINT("BIGINT", listOf(Int::class, Byte::class, Short::class, Long::class)),
    UNSIGNED_BIG_INT(
        "UNSIGNED BIG INT",
        listOf(Int::class, Byte::class, Short::class, Long::class)
    ),
    INT2("INT2", listOf(Int::class, Byte::class, Short::class, Long::class)),
    INT8("INT8", listOf(Int::class, Byte::class, Short::class, Long::class)),
    TEXT("TEXT", listOf(CharSequence::class)),
    CLOB("CLOB", listOf(CharSequence::class)),
    BLOB("BLOB", listOf(ByteArray::class)),
    REAL("REAL", listOf(Float::class, Double::class)),
    DOUBLE("DOUBLE", listOf(Float::class, Double::class)),
    DOUBLE_PRECISION("DOUBLE PRECISION", listOf(Float::class, Double::class)),
    FLOAT("FLOAT", listOf(Float::class, Double::class)),
    NUMERIC("NUMERIC", listOf(Float::class, Double::class)),
    BOOLEAN("BOOLEAN", listOf(Boolean::class)),
    DATE("DATE", listOf(java.util.Date::class, java.sql.Date::class)),
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
