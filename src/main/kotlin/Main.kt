package me.outspending

import me.outspending.connection.MunchConnection
import me.outspending.generator.generateTable
import me.outspending.testing.Test
import java.io.File
import kotlin.time.measureTime

fun main() {
    val database = MunchConnection()

    val time = measureTime {
        database.connect()

        val munch: Munch<Test> = Munch.create(Test::class)
        val clazz: MunchClass<Test> = munch.process()

        database.createTable(clazz)
        println(database.getDataByPrimary(clazz, 1))
    }

    println("Time: $time")
}