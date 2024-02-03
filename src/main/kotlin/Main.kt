package me.outspending

import me.outspending.testing.Test
import kotlin.time.measureTime

fun main() {
    val time = measureTime {
        val munch: Munch<Test> = Munch.create(Test::class)
        val clazz: MunchClass<Test> = munch.process()

        println(clazz.generateTableSQL())
    }

    println("Time: $time")
}