package me.outspending.munch.connection

interface ConnectionTypes<K : Any, V : Any> {
    fun grabData(value: V, runAsync: Boolean = false): K

    fun insertData(obj: K?, runAsync: Boolean = false)
}