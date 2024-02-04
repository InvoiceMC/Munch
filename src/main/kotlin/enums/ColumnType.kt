package me.outspending.enums

/**
 * This class is for all the types that a column can be. You can check what all of these do
 * [here](https://www.sqlite.org/datatype3.html).
 *
 * @param value The name of the type.
 * @since 1.0.0
 * @author Outspending
 */
enum class ColumnType(val value: String) {

    /**
     * This type is used to store text. Which is `"TEXT"` in SQLite.
     *
     * @since 1.0.0
     * @author Outspending
     */
    TEXT("TEXT"),

    /**
     * This type is used to store integers. Which is `"INTEGER"` in SQLite.
     *
     * @since 1.0.0
     * @author Outspending
     */
    INTEGER("INTEGER"),

    /**
     * This type is used to store real numbers. Which is `"REAL"` in SQLite.
     *
     * @since 1.0.0
     * @author Outspending
     */
    REAL("REAL"),

    /**
     * This type is used to store blobs. Which is `"BLOB"` in SQLite.
     *
     * @since 1.0.0
     * @author Outspending
     */
    BLOB("BLOB"),

    /**
     * This type is used for data types that SQLite doesn't support. If you use this type,
     * the data will be stored as a string. And will be serialized / deserialize if the type
     * exists in the Serializer.
     *
     * @since 1.0.0
     * @author Outspending
     */
    OTHER("OTHER")
}