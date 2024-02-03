package me.outspending.testing

import me.outspending.*

@SQLiteTable
data class Test(
    @PrimaryKey(autoIncrement = true) val id: Int,
    @Column(type = ColumnType.TEXT, constraints = [ColumnConstraint.NOTNULL]) val name: String
)
