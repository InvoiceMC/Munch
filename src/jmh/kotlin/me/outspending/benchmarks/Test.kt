package me.outspending.benchmarks

import me.outspending.munch.Column
import me.outspending.munch.ColumnType
import me.outspending.munch.PrimaryKey
import me.outspending.munch.Table

@Table
data class Test(
    @PrimaryKey var id: Int,
    @Column var name: String,
    @Column(ColumnType.BOOLEAN) var isNoob: Boolean
) {
    constructor() : this(0, "", false)
}