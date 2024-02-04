package me.outspending.benchmarks

import me.outspending.*
import me.outspending.enums.ColumnConstraint
import me.outspending.enums.ColumnType

/**
 * This is a test class
 *
 * @param id The id of the test
 * @param name The name of the test
 * @constructor Create empty Test
 */
@Table
data class Test(
    @PrimaryKey(type = ColumnType.INTEGER, autoIncrement = true) val id: Int,
    @Column(type = ColumnType.TEXT, constraints = [ColumnConstraint.NOTNULL]) val name: String,
    @Column(
        type = ColumnType.INTEGER,
        constraints = [ColumnConstraint.NOTNULL, ColumnConstraint.UNIQUE]
    )
    val age: Int
)
