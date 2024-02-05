package me.outspending

import me.outspending.Functions.runAsync
import me.outspending.Functions.toMunch
import me.outspending.connection.MunchConnection
import me.outspending.generator.*
import me.outspending.test.Test
import kotlin.time.*

val database = MunchConnection.create()

fun measureTimeNumerousTimes(times: Int, block: () -> Unit): Duration {
    // Warmup stage fr fr
    repeat(100) {
        block()
    }

    System.gc()

    var totalTime = 0.0
    repeat(times) {
        val time = measureTime {
            block()
        }
        totalTime += time.inWholeNanoseconds
    }

    return (totalTime / times).toDuration(DurationUnit.NANOSECONDS)
}

fun main() {
    database.connect()

    val test: Munch<Test> = Test::class.toMunch()
    val clazz: MunchClass<Test, Int> = test.process()

    printAllGenerators(clazz)
//    val value = Test(10, "Hello, World!", 150)
//    database.addData(clazz, value, true)
}

fun printAllGenerators(clazz: MunchClass<*, *>) {
    println(clazz.generateSelectAll())
    println(clazz.generateSelect())
    println(clazz.generateDeleteAll())
    println(clazz.generateTable())
    println(clazz.generateDelete())
    println(clazz.generateUpdate())
    println(clazz.generateInsert())
}