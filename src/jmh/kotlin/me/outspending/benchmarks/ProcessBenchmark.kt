package me.outspending.benchmarks

import me.outspending.Functions.toMunch
import me.outspending.Munch
import me.outspending.MunchClass
import me.outspending.test.Test
import org.openjdk.jmh.annotations.Benchmark
import org.openjdk.jmh.annotations.BenchmarkMode
import org.openjdk.jmh.annotations.Mode
import org.openjdk.jmh.annotations.Scope
import org.openjdk.jmh.annotations.State
import org.openjdk.jmh.annotations.Warmup
import org.openjdk.jmh.annotations.Measurement

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
