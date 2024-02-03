package me.outspending.processor

import me.outspending.Column
import me.outspending.Munch
import me.outspending.MunchClass
import me.outspending.PrimaryKey
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1

interface MunchProcessor<T : Any> {
    companion object {
        fun <T : Any> create(munch: Munch<T>): MunchProcessor<T> {
            return MunchProcessorImpl(munch)
        }
    }

    fun process(): MunchClass<T>

    fun getPrimaryKey(): Pair<KProperty1<out T, *>, PrimaryKey>?
    fun getColumns(): Map<KProperty1<out T, *>, Column>?
}