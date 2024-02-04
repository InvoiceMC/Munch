package me.outspending.benchmarks

import me.outspending.Munch
import org.openjdk.jmh.annotations.*

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@Warmup(iterations = 3)
@Measurement(iterations = 5)
open class ProcessBenchmark {
    private val munch: Munch<Test> = Munch.create(Test::class)

    @Benchmark
    fun process() {
        munch.process()
    }
}