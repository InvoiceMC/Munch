package me.outspending

import me.outspending.connection.MunchConnection
import me.outspending.generator.generateTable
import me.outspending.testing.Test
import java.io.File
import kotlin.time.measureTime

fun main() {
    val time = measureTime {
        val munch: Munch<Test> = Munch.create(Test::class)
        val clazz: MunchClass<Test> = munch.process()

        println(clazz.generateTable())
    }

    println("Time: $time")
}