package me.outspending.munch.connection

import me.outspending.munch.Functions.runAsyncIf
import me.outspending.munch.MunchClass
import java.io.File
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.concurrent.CompletableFuture

private val connection: Connection by lazy { ConnectionHandler.getConnection() }

class GlobalDatabase internal constructor() : MunchConnection {
    companion object {
        private lateinit var instance: GlobalDatabase

        fun getInstance(): GlobalDatabase {
            if (!this::instance.isInitialized) {
                instance = GlobalDatabase()
            }

            return instance
        }
    }

    override fun connect(databaseName: String, runAsync: Boolean) =
        connect(File(databaseName), runAsync)

    override fun connect(parentPath: File, databaseName: String, runAsync: Boolean) =
        connect(File(parentPath, databaseName), runAsync)

    override fun connect(file: File, runAsync: Boolean) {
        runAsyncIf(runAsync) { ConnectionHandler.connect(file) }
    }

    override fun disconnect() {
        if (isConnected()) return

        connection.close()
    }

    override fun isConnected(): Boolean = ConnectionHandler.isConnected()

    override fun <T : Any> runSQL(sql: String, execute: (PreparedStatement) -> T?): T? {
        if (!isConnected()) throw SQLException("The connection is not open")

        try {
            val statement = connection.prepareStatement(sql)
            return execute(statement)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return null
    }

    override fun <K : Any> createTable(clazz: MunchClass<K, *>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.tableSQL
            runSQL(sql) { statement -> statement.execute() }
        }
    }

    override fun <K : Any> getAllData(clazz: MunchClass<K, *>, runAsync: Boolean): CompletableFuture<List<K>?> {
        return runAsyncIf(runAsync) {
            val sql = clazz.selectAllSQL

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

    override fun <K : Any, V : Any> hasData(
        clazz: MunchClass<K, V>,
        value: V,
        runAsync: Boolean
    ): CompletableFuture<Boolean?> {
        return runAsyncIf(runAsync) {
            val sql = clazz.selectSQL

            runSQL(sql) { statement ->
                setValue(statement, 1, value)

                statement.executeQuery().next()
            }
        }
    }

    override fun <K : Any> addData(clazz: MunchClass<K, *>, obj: K, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.insertSQL
            runSQL(sql) { statement ->
                addValue(statement, obj)

                statement.execute()
            }
        }
    }

    override fun <K : Any> addAllData(clazz: MunchClass<K, *>, obj: Array<K>, runAsync: Boolean) =
        addAllData(clazz, obj.toList(), runAsync)

    override fun <K : Any> addAllData(clazz: MunchClass<K, *>, obj: List<K>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.insertSQL
            runSQL(sql) { statement ->
                for (data in obj) {
                    addValue(statement, data)

                    statement.addBatch()
                }

                statement.executeBatch()
            }
        }
    }

    override fun <K : Any, V : Any> getData(
        clazz: MunchClass<K, V>,
        value: V,
        runAsync: Boolean
    ): CompletableFuture<K?> {
        return runAsyncIf(runAsync) {
            val sql = clazz.selectSQL
            runSQL(sql) { statement ->
                setValue(statement, 1, value)

                val resultSet = statement.executeQuery()
                if (!resultSet.next()) return@runSQL null

                return@runSQL generateType(clazz.clazz, resultSet)
            }
        }
    }

    override fun <K : Any> deleteTable(clazz: MunchClass<K, *>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.deleteTableSQL
            runSQL(sql) { statement -> statement.execute() }
        }
    }

    override fun <K : Any> deleteAllData(clazz: MunchClass<K, *>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.deleteAllSQL
            runSQL(sql) { statement -> statement.execute() }
        }
    }

    override fun <K : Any, V : Any> deleteData(clazz: MunchClass<K, V>, value: V, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.deleteSQL
            runSQL(sql) { statement ->
                setValue(statement, 1, value)
                statement.execute()
            }
        }
    }

    override fun <K : Any, V : Any> updateData(clazz: MunchClass<K, V>, obj: K, value: V, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.updateSQL
            runSQL(sql) { statement ->
                val columns = clazz.columns.keys.toList()

                val currentIndex = addKotlinValues(statement, obj, columns)
                setValue(statement, currentIndex, value)

                statement.execute()
            }
        }
    }

    override fun <K : Any> updateAllData(clazz: MunchClass<K, *>, obj: Array<K>, runAsync: Boolean) =
        updateAllData(clazz, obj.toList(), runAsync)

    override fun <K : Any> updateAllData(clazz: MunchClass<K, *>, obj: List<K>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.updateSQL
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
