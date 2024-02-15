package me.outspending.munch.connection

import me.outspending.munch.MunchClass
import java.io.File
import java.sql.PreparedStatement
import java.util.concurrent.CompletableFuture

class MunchDatabase<K : Any, V : Any> internal constructor(private val clazz: MunchClass<K, V>) {
    private val database = GlobalDatabase()

    fun connect(databaseName: String, runAsync: Boolean) = connect(File(databaseName), runAsync)

    fun connect(parentPath: File, databaseName: String, runAsync: Boolean) =
        connect(File(parentPath, databaseName), runAsync)

    fun connect(file: File, runAsync: Boolean) = database.connect(file, runAsync)

    fun disconnect() = database.disconnect()

    fun isConnected(): Boolean = database.isConnected()

    fun <T : Any> runSQL(sql: String, execute: (PreparedStatement) -> T?): T? =
        database.runSQL(sql, execute)

    fun createTable(runAsync: Boolean) = database.createTable(clazz, runAsync)

    fun getAllData(runAsync: Boolean): CompletableFuture<List<K>?> =
        database.getAllData(clazz, runAsync)

    fun hasData(value: V, runAsync: Boolean): CompletableFuture<Boolean?> =
        database.hasData(clazz, value, runAsync)

    fun addData(obj: K, runAsync: Boolean) = database.addData(clazz, obj, runAsync)

    fun addAllData(obj: Array<K>, runAsync: Boolean) = addAllData(obj.toList(), runAsync)

    fun addAllData(obj: List<K>, runAsync: Boolean) = database.addAllData(clazz, obj, runAsync)

    fun getData(value: V, runAsync: Boolean): CompletableFuture<K?> =
        database.getData(clazz, value, runAsync)

    fun deleteTable(runAsync: Boolean) = database.deleteTable(clazz, runAsync)

    fun deleteAllData(runAsync: Boolean) = database.deleteAllData(clazz, runAsync)

    fun deleteData(value: V, runAsync: Boolean) = database.deleteData(clazz, value, runAsync)

    fun updateData(obj: K, value: V, runAsync: Boolean) =
        database.updateData(clazz, obj, value, runAsync)

    fun updateAllData(obj: Array<K>, runAsync: Boolean) = updateAllData(obj.toList(), runAsync)

    fun updateAllData(obj: List<K>, runAsync: Boolean) =
        database.updateAllData(clazz, obj, runAsync)
}
