package me.outspending

import me.outspending.connection.MunchConnection
import me.outspending.test.Test
import kotlin.time.measureTime

val database = MunchConnection()

fun main() {
    database.connect()

    val test: Munch<Test> = Munch(Test::class)
    val clazz: MunchClass<Test, Int> = test.process()

    val time = measureTime {
        val something = database.getData(clazz, 10)
        println(something)
    }
    println("Finished in $time")
}