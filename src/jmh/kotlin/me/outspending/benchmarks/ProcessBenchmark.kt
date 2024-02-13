package me.outspending.benchmarks

import me.outspending.munch.Munch
import me.outspending.munch.MunchClass
import me.outspending.munch.generator.generateTable
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
open class ProcessBenchmark {
    private val munch: Munch<Test> = Munch.create(Test::class)
    private val munchClass: MunchClass<Test, Int> = munch.process()

    @Benchmark
    fun process() {
        val munchClass: MunchClass<Test, Int> = munch.process()
        println("Benchmark result: $munchClass")
    }

    @Benchmark
    fun generateTable() {
        println(munchClass.generateTable())
    }


}
