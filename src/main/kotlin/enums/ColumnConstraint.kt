package me.outspending.enums

/**
 * This class is for all the constraints that can be applied to a column. You can check what all of
 * these do [here](https://www.sqlite.org/syntax/table-constraint.html).
 *
 * @param value The name of the constraint.
 * @author Outspending
 * @since 1.0.0
 */
enum class ColumnConstraint(val value: String) {

    /**
     * This constraint is used to make sure that the column is not null. Which is `"NOT NULL"` in
     * SQLite.
     *
     * @author Outspending
     * @since 1.0.0
     */
    NOTNULL("NOT NULL"),

    /**
     * This constraint is used to make sure that the column is unique. Which is `"UNIQUE"` in
     * SQLite.
     *
     * @author Outspending
     * @since 1.0.0
     */
    UNIQUE("UNIQUE"),

    /**
     * This constraint is used to make sure that the column is the primary key. Which is `"CHECK"`
     * in SQLite.
     *
     * @author Outspending
     * @since 1.0.0
     */
    CHECK("CHECK"),

    /**
     * This constraint is used to make sure that the column is the primary key. Which is `"FOREIGN
     * KEY"` in SQLite.
     *
     * @author Outspending
     * @since 1.0.0
     */
    FOREIGN("FOREIGN KEY"),
}
