package me.outspending.munch.connection

import me.outspending.munch.MunchClass
import java.io.File
import java.sql.PreparedStatement
import java.util.concurrent.CompletableFuture

class MunchDatabase<K : Any, V : Any> internal constructor(private val clazz: MunchClass<K, V>) {
    private val database = GlobalDatabase.getInstance()

    fun connect(databaseName: String = "database.db", runAsync: Boolean = false) = connect(File(databaseName), runAsync)

    fun connect(parentPath: File, databaseName: String, runAsync: Boolean = false) =
        connect(File(parentPath, databaseName), runAsync)

    fun connect(file: File, runAsync: Boolean = false) = database.connect(file, runAsync)

    fun disconnect() = database.disconnect()

    fun isConnected(): Boolean = database.isConnected()

    fun <T : Any> runSQL(sql: String, execute: (PreparedStatement) -> T?): T? =
        database.runSQL(sql, execute)

    fun createTable(runAsync: Boolean = false) = database.createTable(clazz, runAsync)

    fun getAllData(runAsync: Boolean = false): CompletableFuture<List<K>?> =
        database.getAllData(clazz, runAsync)

    fun <K : Any, V : Any> getAllDataWithFilter(clazz: MunchClass<K, V>, filter: (K) -> Boolean, runAsync: Boolean = false): CompletableFuture<List<K?>?> =
        database.getAllDataWithFilter(clazz, filter, runAsync)

    fun hasData(value: V, runAsync: Boolean = false): CompletableFuture<Boolean?> =
        database.hasData(clazz, value, runAsync)

    fun addData(obj: K, runAsync: Boolean = false) = database.addData(clazz, obj, runAsync)

    fun addAllData(obj: Array<K>, runAsync: Boolean = false) = addAllData(obj.toList(), runAsync)

    fun addAllData(obj: List<K>, runAsync: Boolean = false) = database.addAllData(clazz, obj, runAsync)

    fun getData(value: V, runAsync: Boolean = false): CompletableFuture<K?> =
        database.getData(clazz, value, runAsync)

    fun deleteTable(runAsync: Boolean = false) = database.deleteTable(clazz, runAsync)

    fun deleteAllData(runAsync: Boolean = false) = database.deleteAllData(clazz, runAsync)

    fun deleteData(value: V, runAsync: Boolean = false) = database.deleteData(clazz, value, runAsync)

    fun updateData(obj: K, value: V, runAsync: Boolean = false) =
        database.updateData(clazz, obj, value, runAsync)

    fun updateAllData(obj: Array<K>, runAsync: Boolean = false) = updateAllData(obj.toList(), runAsync)

    fun updateAllData(obj: List<K>, runAsync: Boolean = false) =
        database.updateAllData(clazz, obj, runAsync)
}
