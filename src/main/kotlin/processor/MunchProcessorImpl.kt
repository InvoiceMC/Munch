package me.outspending.processor

import me.outspending.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.declaredMembers

class MunchProcessorImpl<T : Any>(val munch: Munch<T>) : MunchProcessor<T> {
    override fun process(): MunchClass<T> {
        val primaryKey = getPrimaryKey()
        val columns = getColumns()

        return MunchClass(munch.getClass(), primaryKey, columns)
    }

    override fun getPrimaryKey(): Pair<KProperty1<out T, *>, PrimaryKey>? {
        val primaryKey = munch.getProperties()
            .filter { it.annotations.any { annotation -> annotation is PrimaryKey } }
            .toList()

        if (primaryKey.size > 1) throw IllegalArgumentException("Multiple primary keys found")
        if (primaryKey.isEmpty()) return null

        val main = primaryKey[0]
        return (main to main.annotations.first { it is PrimaryKey } as PrimaryKey)
    }

    override fun getColumns(): Map<KProperty1<out T, *>, Column>? {
        val columns = munch.getProperties()
            .filter { it.annotations.any { annotation -> annotation is Column } }
            .toList()

        if (columns.isEmpty()) return null

        return columns.associateWith { it.annotations.first { column -> column is Column } as Column }
    }
}