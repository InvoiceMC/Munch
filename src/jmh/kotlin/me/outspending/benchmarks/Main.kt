package me.outspending.benchmarks

fun main() {
    println("Testing null")
    val nullTest = NullTest()
    nullTest.test()
    println("Done testing null. Running teardown.")
    nullTest.teardown()
}