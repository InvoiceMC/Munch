package me.outspending.generator.types

interface AllGenerator<T : Any> : Generator<T>, ColumnGenerator<T>, PrimaryGenerator<T>
