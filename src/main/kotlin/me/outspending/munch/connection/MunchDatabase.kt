package me.outspending.munch.connection

import me.outspending.munch.Functions.runAsyncIf
import me.outspending.munch.MunchClass
import me.outspending.munch.generator.*
import java.io.File
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.concurrent.CompletableFuture

class MunchDatabase<K : Any, V : Any> internal constructor(private val clazz: MunchClass<K, V>) :
    MunchConnection<K, V> {

    private val tableSQL: String by lazy { clazz.generateTable() }

    private val selectAllSQL: String by lazy { clazz.generateSelectAll() }
    private val selectSQL: String by lazy { clazz.generateSelect() }

    private val updateSQL: String by lazy { clazz.generateUpdate() }
    private val insertSQL: String by lazy { clazz.generateInsert() }

    private val deleteTableSQL: String by lazy { clazz.generateDeleteTable() }
    private val deleteAllSQL: String by lazy { clazz.generateDeleteAll() }
    private val deleteSQL: String by lazy { clazz.generateDelete() }

    private val connection: Connection by lazy { ConnectionHandler.getConnection() }

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

    override fun createTable(runAsync: Boolean) {
        runAsyncIf(runAsync) { runSQL(tableSQL) { statement -> statement.execute() } }
    }

    override fun getAllData(runAsync: Boolean): CompletableFuture<List<K>?> {
        return runAsyncIf(runAsync) {
            runSQL(selectAllSQL) { statement ->
                val resultSet = statement.executeQuery()

                val list = mutableListOf<K>()
                while (resultSet.next()) {
                    generateType(clazz.clazz, resultSet)?.let { list.add(it) }
                }

                list
            }
        }
    }

    override fun hasData(value: V, runAsync: Boolean): CompletableFuture<Boolean?> {
        return runAsyncIf(runAsync) {
            runSQL(selectSQL) { statement ->
                setValue(statement, 1, value)

                statement.executeQuery().next()
            }
        }
    }

    override fun addData(obj: K, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            runSQL(insertSQL) { statement ->
                addValue(statement, obj)

                statement.execute()
            }
        }
    }

    override fun addAllData(obj: Array<K>, runAsync: Boolean) = addAllData(obj.toList(), runAsync)

    override fun addAllData(obj: List<K>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            runSQL(insertSQL) { statement ->
                for (data in obj) {
                    addValue(statement, data)

                    statement.addBatch()
                }

                statement.executeBatch()
            }
        }
    }

    override fun getData(value: V, runAsync: Boolean): CompletableFuture<K?> {
        return runAsyncIf(runAsync) {
            runSQL(selectSQL) { statement ->
                setValue(statement, 1, value)

                val resultSet = statement.executeQuery()
                if (!resultSet.next()) return@runSQL null

                return@runSQL generateType(clazz.clazz, resultSet)
            }
        }
    }

    override fun deleteTable(runAsync: Boolean) {
        runAsyncIf(runAsync) {
            runSQL(deleteTableSQL) { statement -> statement.execute() }
        }
    }

    override fun deleteAllData(runAsync: Boolean) {
        runAsyncIf(runAsync) {
            runSQL(deleteAllSQL) { statement -> statement.execute() }
        }
    }

    override fun deleteData(value: V, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            runSQL(deleteSQL) { statement ->
                setValue(statement, 1, value)
                statement.execute()
            }
        }
    }

    override fun updateData(obj: K, value: V, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            runSQL(updateSQL) { statement ->
                val columns = clazz.columns.keys.toList()

                val currentIndex = addKotlinValues(statement, obj, columns)
                setValue(statement, currentIndex, value)

                statement.execute()
            }
        }
    }

    override fun updateAllData(obj: Array<K>, runAsync: Boolean) =
        updateAllData(obj.toList(), runAsync)

    override fun updateAllData(obj: List<K>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            runSQL(updateSQL) { statement ->
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
