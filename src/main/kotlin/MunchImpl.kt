package me.outspending

import me.outspending.processor.MunchProcessor
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties

class MunchImpl<T : Any>(private val clazz: KClass<T>) : Munch<T> {
    private val munchProcessor: MunchProcessor<T> = MunchProcessor.create(this)

    override fun process(): MunchClass<T> {
        if (isSQLType()) return munchProcessor.process()
        else throw IllegalArgumentException("Class is not an SQL type")
    }

    override fun isSQLType(): Boolean = clazz.annotations.any { it is Table }
    override fun getClass(): KClass<T> = clazz
    override fun getProperties(): Collection<KProperty1<out T, *>> = clazz.memberProperties
}