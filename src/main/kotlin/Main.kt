package me.outspending

import me.outspending.generator.*
import me.outspending.test.Test
import kotlin.time.measureTime

fun main() {
    val test = Munch.create(Test::class)
    val clazz = test.process()

    val time = measureTime {
        println(clazz.generateTable())
        println(clazz.generateInsert())
        println(clazz.generateUpdate())
        println(clazz.generateDelete())
        println(clazz.generateSelect())
        println(clazz.generateSelectAll())
    }
    println("Finished in $time")
}