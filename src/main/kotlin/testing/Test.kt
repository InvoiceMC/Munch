package me.outspending.testing

import me.outspending.*

/**
 * This is a test class
 *
 * @param id The id of the test
 * @param name The name of the test
 * @constructor Create empty Test
 */
@Table
data class Test(
    @PrimaryKey(autoIncrement = true) val id: Int,
    @Column(type = ColumnType.TEXT, constraints = [ColumnConstraint.NOTNULL]) val name: String
)
