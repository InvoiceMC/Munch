package me.outspending.munch

import me.outspending.munch.generator.generateTable

@Table
data class Test(
    @PrimaryKey var id: Int,
    @Column var name: String,
    @Column(ColumnType.BOOLEAN) var isNoob: Boolean
) {
    constructor() : this(0, "", false)
}

fun main() {
    val munch = Munch.create(Test::class)
    val munchClass = munch.process<Int>()

    println(munchClass.generateTable())
}