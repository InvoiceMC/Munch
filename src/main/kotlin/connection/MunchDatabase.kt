package me.outspending.connection

import me.outspending.Functions.runAsyncIf
import me.outspending.MunchClass
import me.outspending.generator.*
import java.io.File
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException

class MunchDatabase : MunchConnection {
    companion object {
        private lateinit var connection: Connection
    }

    override fun connect(databaseName: String, runAsync: Boolean) =
        connect(File(databaseName), runAsync)

    override fun connect(parentPath: File, databaseName: String, runAsync: Boolean) =
        connect(File(parentPath, databaseName), runAsync)

    override fun connect(file: File, runAsync: Boolean) {
        try {
            if (!file.exists()) {
                file.createNewFile()
            }

            val connectionURL = "jdbc:sqlite:${file.absolutePath}"
            connection = DriverManager.getConnection(connectionURL)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun <T : Any> runSQL(sql: String, execute: (PreparedStatement) -> T?): T? {
        try {
            val statement = connection.prepareStatement(sql)
            return execute(statement)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return null
    }

    override fun <T : Any, K : Any> createTable(clazz: MunchClass<T, K>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateTable()
            runSQL(sql) { statement -> statement.execute() }
        }
    }

    override fun <T : Any, K : Any> getAllData(
        clazz: MunchClass<T, K>,
        runAsync: Boolean
    ): List<T>? {
        return runAsyncIf(runAsync) {
            val sql = clazz.generateSelectAll()
            runSQL(sql) { statement ->
                val resultSet = statement.executeQuery()

                val list = mutableListOf<T>()
                while (resultSet.next()) {
                    generateType(clazz.clazz, resultSet)?.let { list.add(it) }
                }

                list
            }
        }
    }

    override fun <T : Any, K : Any> hasData(
        clazz: MunchClass<T, K>,
        value: K,
        runAsync: Boolean
    ): Boolean? {
        return runAsyncIf(runAsync) {
            val sql = clazz.generateSelect()

            runSQL(sql) { statement ->
                setValue(statement, 1, value)

                statement.executeQuery().next()
            }
        }
    }

    override fun <T : Any, K : Any> addData(clazz: MunchClass<T, K>, obj: T, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateInsert()
            runSQL(sql) { statement ->
                addValue(statement, obj)

                statement.execute()
            }
        }
    }

    override fun <T : Any, K : Any> addAllData(
        clazz: MunchClass<T, K>,
        obj: Array<T>,
        runAsync: Boolean
    ) = addAllData(clazz, obj.toList(), runAsync)

    override fun <T : Any, K : Any> addAllData(
        clazz: MunchClass<T, K>,
        obj: List<T>,
        runAsync: Boolean
    ) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateInsert()
            runSQL(sql) { statement ->
                for (data in obj) {
                    addValue(statement, obj)

                    statement.addBatch()
                }

                statement.executeBatch()
            }
        }
    }

    override fun <T : Any, K : Any> getData(
        clazz: MunchClass<T, K>,
        value: K,
        runAsync: Boolean
    ): T? {
        val sql = clazz.generateSelect()
        return runSQL(sql) { statement ->
            setValue(statement, 1, value)

            val resultSet = statement.executeQuery()
            if (!resultSet.next()) return@runSQL null

            generateType(clazz.clazz, resultSet)
        }
    }

    override fun <T : Any, K : Any> deleteTable(clazz: MunchClass<T, K>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateDeleteTable()
            runSQL(sql) { statement -> statement.execute() }
        }
    }

    override fun <T : Any, K : Any> deleteAllData(clazz: MunchClass<T, K>, runAsync: Boolean) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateDeleteAll()
            runSQL(sql) { statement -> statement.execute() }
        }
    }

    override fun <T : Any, K : Any> deleteData(
        clazz: MunchClass<T, K>,
        value: K,
        runAsync: Boolean
    ) {
        runAsyncIf(runAsync) {
            val sql = clazz.generateDelete()

            runSQL(sql) { statement ->
                setValue(statement, 1, value)
                statement.execute()
            }
        }
    }
}
