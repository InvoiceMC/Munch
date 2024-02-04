package me.outspending

import me.outspending.connection.MunchConnection
import me.outspending.test.Test
import kotlin.time.measureTime

val database = MunchConnection()

fun main() {
    database.connect()

    val test: Munch<Test> = Munch.create(Test::class)
    val clazz: MunchClass<Test> = test.process()

    val time = measureTime {
//        val data = Test(1, "Hello, World!", 10)
//        database.addData(clazz, data)

        val something = database.getData(clazz, 10)
        println(something)
    }
    println("Finished in $time")
}