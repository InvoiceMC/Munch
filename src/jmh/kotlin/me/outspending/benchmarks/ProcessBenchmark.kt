package me.outspending.benchmarks

import me.outspending.Munch
import me.outspending.MunchClass
import me.outspending.munch.Functions.toMunch
import me.outspending.test.Test
import org.openjdk.jmh.annotations.*

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
open class ProcessBenchmark {
    private val munch: Munch<Test> = Test::class.toMunch()

    @Benchmark
    fun process() {
        val munchClass: MunchClass<Test, Int> = munch.process()
        // Use JMH logging to output benchmark results
        println("Benchmark result: $munchClass")
    }
}
