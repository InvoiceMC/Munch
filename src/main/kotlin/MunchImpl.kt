package me.outspending

import me.outspending.processor.MunchProcessor
import kotlin.reflect.KClass

class MunchImpl<T : Any>(val clazz: KClass<T>) : Munch<T> {
    private val munchProcessor: MunchProcessor<T> = MunchProcessor.create(this)

    override fun process(): MunchClass<T> {
        TODO("Not yet implemented")
    }

    override fun isSQLType(): Boolean {
        return this.clazz.annotations.any { it is SQLiteTable }
    }

    override fun hasPrimaryKey(): Boolean {

    }

    override fun getClass(): KClass<T> {
        if (clazz.isData) return clazz
        else throw IllegalArgumentException("Munch only supports data classes")
    }
}