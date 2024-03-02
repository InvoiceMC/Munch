package me.outspending.munch.connection

import me.outspending.munch.MunchClass
import java.io.File
import java.sql.PreparedStatement
import java.util.concurrent.CompletableFuture

class MunchDatabase<K : Any, V : Any> internal constructor(private val clazz: MunchClass<K, V>) {
    private val database = GlobalDatabase.getInstance()

    fun connect(databaseName: String = "database.db") = connect(File(databaseName))

    fun connect(parentPath: File, databaseName: String) =
        connect(File(parentPath, databaseName))

    fun connect(file: File) = database.connect(file)

    fun disconnect() = database.disconnect()

    fun isConnected(): Boolean = database.isConnected()

    fun <T : Any> runSQL(sql: String, execute: (PreparedStatement) -> T?): T? =
        database.runSQL(sql, execute)

    fun createTable() = database.createTable(clazz)

    fun getAllData(): MutableList<K>? =
        database.getAllData(clazz)

    fun hasData(value: V): Boolean? =
        database.hasData(clazz, value)

    fun addData(obj: K) = database.addData(clazz, obj)

    fun addAllData(obj: Array<K>) = addAllData(obj.toList())

    fun addAllData(obj: List<K>) = database.addAllData(clazz, obj)

    fun getData(value: V): K? =
        database.getData(clazz, value)

    fun getAllData(value: V): MutableList<K>? =
        database.getAllData(clazz, value)

    fun <K : Any, V : Any> getAllDataWithFilter(clazz: MunchClass<K, V>, filter: (K) -> Boolean): List<K>? =
        database.getAllDataWithFilter(clazz, filter)

    fun deleteTable() = database.deleteTable(clazz)

    fun deleteAllData() = database.deleteAllData(clazz)

    fun deleteData(value: V) = database.deleteData(clazz, value)

    fun updateData(obj: K, value: V) =
        database.updateData(clazz, obj, value)

    fun updateAllData(obj: Array<K>) = updateAllData(obj.toList())

    fun updateAllData(obj: List<K>) =
        database.updateAllData(clazz, obj)
}
