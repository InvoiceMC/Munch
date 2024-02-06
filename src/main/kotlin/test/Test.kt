package me.outspending.test

import me.outspending.Column
import me.outspending.ColumnConstraint
import me.outspending.PrimaryKey
import me.outspending.Table

/**
 * This is a test class
 *
 * @param id The id of the test
 * @param name The name of the test
 * @constructor Create empty Test
 */
@Table
data class Test(
    @PrimaryKey(autoIncrement = true) var id: Int,
    @Column(constraints = [ColumnConstraint.NOTNULL]) var name: String,
    @Column(constraints = [ColumnConstraint.NOTNULL, ColumnConstraint.UNIQUE]) var age: Int,
) {
    constructor() : this(0, "", 0)
}
