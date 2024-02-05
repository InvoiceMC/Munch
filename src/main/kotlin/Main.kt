package me.outspending

import me.outspending.Functions.toMunch
import me.outspending.connection.MunchConnection
import me.outspending.generator.generateTable
import me.outspending.test.Test
import kotlin.time.measureTime

val database = MunchConnection()
fun main() {
    database.connect()

    val test: Munch<Test> = Test::class.toMunch()
    val clazz: MunchClass<Test, Int> = test.process()

    val time = measureTime {
        // val test = Test(1, "Test", 10)

        val data = database.getData(clazz, 1)
        println(data)
    }

    println("Finished in $time")
}