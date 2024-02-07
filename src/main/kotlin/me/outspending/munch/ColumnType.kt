package me.outspending.munch

import kotlin.reflect.KClass

enum class ColumnType(val value: String, val type: ValueType?) {
    // Numbers
    INT(
        "INT",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Int::class, Byte::class, Short::class, Long::class)
            }
        }
    ),
    INTEGER(
        "INTEGER",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Int::class, Byte::class, Short::class, Long::class)
            }
        }
    ),
    TINYINT(
        "TINYINT",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Byte::class, Short::class)
            }
        }
    ),
    SMALLINT(
        "SMALLINT",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Int::class, Byte::class, Short::class)
            }
        }
    ),
    MEDIUMINT(
        "MEDIUMINT",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Int::class, Byte::class, Short::class, Long::class)
            }
        }
    ),
    BIGINT(
        "BIGINT",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Int::class, Byte::class, Short::class, Long::class)
            }
        }
    ),
    UNSIGNED_BIG_INT(
        "UNSIGNED BIG INT",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Int::class, Byte::class, Short::class, Long::class)
            }
        }
    ),
    INT2(
        "INT2",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Int::class, Byte::class, Short::class, Long::class)
            }
        }
    ),
    INT8(
        "INT8",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Int::class, Byte::class, Short::class, Long::class)
            }
        }
    ),

    // Strings
    CHARACTER20(
        "CHARACTER(20)",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(CharSequence::class)
            }
        }
    ),
    VARCHAR(
        "VARCHAR(255)",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(CharSequence::class)
            }
        }
    ),
    VARYING_CHARACTER(
        "VARYING CHARACTER(255)",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(CharSequence::class)
            }
        }
    ),
    NCHAR(
        "NCHAR(55)",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(CharSequence::class)
            }
        }
    ),
    NATIVE_CHARACTER(
        "NATIVE CHARACTER(70)",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(CharSequence::class)
            }
        }
    ),
    NVCHAR(
        "NVCHAR(100)",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(CharSequence::class)
            }
        }
    ),
    TEXT(
        "TEXT",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(CharSequence::class)
            }
        }
    ),
    CLOB(
        "CLOB",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(CharSequence::class)
            }
        }
    ),

    // Blob
    BLOB(
        "BLOB",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(ByteArray::class)
            }
        }
    ),

    // Real Numbers
    REAL(
        "REAL",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Float::class, Double::class)
            }
        }
    ),
    DOUBLE(
        "DOUBLE",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Float::class, Double::class)
            }
        }
    ),
    DOUBLE_PRECISION(
        "DOUBLE PRECISION",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Float::class, Double::class)
            }
        }
    ),
    FLOAT(
        "FLOAT",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Float::class, Double::class)
            }
        }
    ),

    // Numeric
    NUMERIC(
        "NUMERIC",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Float::class, Double::class)
            }
        }
    ),
    DECIMAL(
        "DECIMAL(10,5)",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Float::class, Double::class)
            }
        }
    ),
    BOOLEAN(
        "BOOLEAN",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(Boolean::class)
            }
        }
    ),
    DATE(
        "DATE",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(java.util.Date::class, java.sql.Date::class)
            }
        }
    ),
    DATETIME(
        "DATETIME",
        object : ValueType {
            override fun getSupportedTypes(): List<KClass<*>> {
                return listOf(java.util.Date::class, java.sql.Date::class)
            }
        }
    ),

    /**
     * This [ColumnType] represents a column that defaults to the classifier's default value. If you
     * don't want to use ColumnType's default value, use [NONE].
     *
     * @author Outspending
     * @since 1.0.0
     */
    NONE("NONE", null)
}
