package me.outspending.munch.connection

import me.outspending.munch.Functions.runAsyncIf
import me.outspending.munch.MunchClass
import me.outspending.munch.generator.*
import java.io.File
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.concurrent.CompletableFuture

class MunchDatabase<K : Any, V : Any> internal constructor(private val clazz: MunchClass<K, V>) :
    MunchConnection<K, V> {

    private val tableSQL: String by lazy { clazz.generateTable() }
    private val selectAllSQL: String by lazy { clazz.generateSelectAll() }

    private val connection by lazy { ConnectionHandler.getConnection() }

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

    @Suppress("UNCHECKED_CAST")
    override fun getAllData(runAsync: Boolean): CompletableFuture<List<V>?> {
        return runAsyncIf(runAsync) {
            runSQL(selectAllSQL) { statement ->
                val resultSet = statement.executeQuery()

                val list = mutableListOf<V>()
                while (resultSet.next()) {
                    generateType(clazz.clazz, resultSet)?.let { list.add(it as V) }
                }

                list
            }
        }
    }

    override fun hasData(value: V, runAsync: Boolean): CompletableFuture<Boolean?> {
        return runAsyncIf(runAsync) {
            val sql = clazz.generateSelect()

            runSQL(sql) { statement ->
                setValue(statement, 1, value)

                statement.executeQuery().next()
            }
        }
    }

    override fun addData(obj: K, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateInsert()
            runSQL(sql) { statement ->
                addValue(statement, obj)

                statement.execute()
            }
        }
    }

    override fun addAllData(obj: Array<K>, runAsync: Boolean) = addAllData(obj.toList(), runAsync)

    override fun addAllData(obj: List<K>, runAsync: Boolean) {
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

    override fun getData(value: V, runAsync: Boolean): CompletableFuture<K?> {
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

    override fun deleteTable(runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateDeleteTable()
            runSQL(sql) { statement -> statement.execute() }
        }
    }

    override fun deleteAllData(runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateDeleteAll()
            runSQL(sql) { statement -> statement.execute() }
        }
    }

    override fun deleteData(value: V, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateDelete()

            runSQL(sql) { statement ->
                setValue(statement, 1, value)
                statement.execute()
            }
        }
    }

    override fun updateData(obj: K, value: V, runAsync: Boolean) {
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

    override fun updateAllData(obj: Array<K>, runAsync: Boolean) =
        updateAllData(obj.toList(), runAsync)

    override fun updateAllData(obj: List<K>, runAsync: Boolean) {
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
