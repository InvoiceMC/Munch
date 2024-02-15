package me.outspending.munch.connection

import me.outspending.munch.Functions.runAsyncIf
import me.outspending.munch.MunchClass
import me.outspending.munch.generator.*
import java.io.File
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.concurrent.CompletableFuture

class GlobalDatabase<K : Any, V : Any> {
    companion object {
        private val connection: Connection by lazy { ConnectionHandler.getConnection() }
    }

    fun connect(databaseName: String, runAsync: Boolean) = connect(File(databaseName), runAsync)

    fun connect(parentPath: File, databaseName: String, runAsync: Boolean) =
        connect(File(parentPath, databaseName), runAsync)

    fun connect(file: File, runAsync: Boolean) {
        runAsyncIf(runAsync) { ConnectionHandler.connect(file) }
    }

    fun disconnect() {
        if (isConnected()) return

        connection.close()
    }

    fun isConnected(): Boolean = ConnectionHandler.isConnected()

    fun <T : Any> runSQL(sql: String, execute: (PreparedStatement) -> T?): T? {
        if (!isConnected()) throw SQLException("The connection is not open")

        try {
            val statement = connection.prepareStatement(sql)
            return execute(statement)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return null
    }

    fun createTable(clazz: MunchClass<K, V>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateTable()
            runSQL(sql) { statement -> statement.execute() }
        }
    }

    fun getAllData(clazz: MunchClass<K, V>, runAsync: Boolean): CompletableFuture<List<K>?> {
        return runAsyncIf(runAsync) {
            val sql = clazz.generateSelectAll()

            runSQL(sql) { statement ->
                val resultSet = statement.executeQuery()

                val list = mutableListOf<K>()
                while (resultSet.next()) {
                    generateType(clazz.clazz, resultSet)?.let { list.add(it) }
                }

                list
            }
        }
    }

    fun hasData(clazz: MunchClass<K, V>, runAsync: Boolean): CompletableFuture<Boolean?> {
        return runAsyncIf(runAsync) {
            val sql = clazz.generateSelect()

            runSQL(sql) { statement ->
                setValue(statement, 1, value)

                statement.executeQuery().next()
            }
        }
    }

    fun addData(clazz: MunchClass<K, V>, obj: K, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateInsert()
            runSQL(sql) { statement ->
                addValue(statement, obj)

                statement.execute()
            }
        }
    }

    fun addAllData(clazz: MunchClass<K, V>, obj: Array<K>, runAsync: Boolean) =
        addAllData(clazz, obj.toList(), runAsync)

    fun addAllData(clazz: MunchClass<K, V>, obj: List<K>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateInsert()
            runSQL(sql) { statement ->
                for (data in obj) {
                    addValue(statement, data)

                    statement.addBatch()
                }

                statement.executeBatch()
            }
        }
    }

    fun getData(clazz: MunchClass<K, V>, value: V, runAsync: Boolean): CompletableFuture<K?> {
        return runAsyncIf(runAsync) {
            val sql = clazz.generateSelect()
            runSQL(sql) { statement ->
                setValue(statement, 1, value)

                val resultSet = statement.executeQuery()
                if (!resultSet.next()) return@runSQL null

                return@runSQL generateType(clazz.clazz, resultSet)
            }
        }
    }

    fun deleteTable(clazz: MunchClass<K, V>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateDeleteTable()
            runSQL(sql) { statement -> statement.execute() }
        }
    }

    fun deleteAllData(clazz: MunchClass<K, V>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateDeleteAll()
            runSQL(sql) { statement -> statement.execute() }
        }
    }

    fun deleteData(clazz: MunchClass<K, V>, value: V, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateDelete()
            runSQL(sql) { statement ->
                setValue(statement, 1, value)
                statement.execute()
            }
        }
    }

    fun updateData(clazz: MunchClass<K, V>, obj: K, value: V, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateUpdate()
            runSQL(sql) { statement ->
                val columns = clazz.columns.keys.toList()

                val currentIndex = addKotlinValues(statement, obj, columns)
                setValue(statement, currentIndex, value)

                statement.execute()
            }
        }
    }

    fun updateAllData(clazz: MunchClass<K, V>, obj: Array<K>, runAsync: Boolean) = updateAllData(clazz, obj.toList(), runAsync)

    fun updateAllData(clazz: MunchClass<K, V>, obj: List<K>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateUpdate()
            runSQL(sql) { statement ->
                for (data in obj) {
                    val key = clazz.primaryKey.first.call(data)
                    val columns = clazz.columns.keys.toList()

                    val currentIndex = addKotlinValues(statement, data, columns)
                    key?.let { setValue(statement, currentIndex, it) }

                    statement.addBatch()
                }

                statement.executeBatch()
            }
        }
    }
}
