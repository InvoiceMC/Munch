package me.outspending.munch.connection

import me.outspending.munch.MunchClass
import java.io.File
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.SQLException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

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

    override fun connect(databaseName: String) =
        connect(File(databaseName))

    override fun connect(parentPath: File, databaseName: String) =
        connect(File(parentPath, databaseName))

    override fun connect(file: File) = ConnectionHandler.connect(file)

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

    override fun <K : Any> createTable(clazz: MunchClass<K, *>) {
        val sql = clazz.tableSQL
        runSQL(sql) { statement -> statement.execute() }
    }

    override fun <K : Any> getAllData(clazz: MunchClass<K, *>): MutableList<K>? {
        val sql = clazz.selectAllSQL

        return runSQL(sql) { statement ->
            val resultSet = statement.executeQuery()

            val list = mutableListOf<K>()
            while (resultSet.next()) {
                generateType(clazz.clazz, resultSet)?.let { list.add(it) }
            }

            list
        }
    }

    override fun <K : Any, V : Any> hasData(
        clazz: MunchClass<K, V>,
        value: V
    ): Boolean {
        val sql = clazz.selectSQL

        return runSQL(sql) { statement ->
            setValue(statement, 1, value)

            statement.executeQuery().next()
        } ?: false
    }

    override fun <K : Any> addData(clazz: MunchClass<K, *>, obj: K) {
        val sql = clazz.insertSQL
        runSQL(sql) { statement ->
            addValue(statement, obj)

            statement.execute()
        }
    }

    override fun <K : Any> addAllData(clazz: MunchClass<K, *>, obj: Array<K>) =
        addAllData(clazz, obj.toList())

    override fun <K : Any> addAllData(clazz: MunchClass<K, *>, obj: List<K>) {
        val sql = clazz.insertSQL
        runSQL(sql) { statement ->
            for (data in obj) {
                addValue(statement, data)

                statement.addBatch()
            }

            statement.executeBatch()
        }
    }

    override fun <K : Any, V : Any> getData(
        clazz: MunchClass<K, V>,
        value: V
    ): K? {
        val sql = clazz.selectSQL
        return runSQL(sql) { statement ->
            setValue(statement, 1, value)

            val resultSet = statement.executeQuery()
            if (!resultSet.next()) return@runSQL null

            generateType(clazz.clazz, resultSet)
        }
    }

    override fun <K : Any, V : Any> getAllDataWithFilter(
        clazz: MunchClass<K, V>,
        filter: (K) -> Boolean
    ): List<K>? {
        val allData = getAllData(clazz)
        return allData?.filter(filter)
    }

    override fun <K : Any, V : Any> getAllData(
        clazz: MunchClass<K, V>,
        value: V
    ): MutableList<K>? {
        val sql = clazz.selectSQL
        return runSQL(sql) { statement ->
            setValue(statement, 1, value)

            val list = mutableListOf<K>()
            val resultSet = statement.executeQuery()
            while (resultSet.next()) {
                generateType(clazz.clazz, resultSet)?.let { list.add(it) }
            }

            list
        }
    }

    override fun <K : Any> deleteTable(clazz: MunchClass<K, *>) {
        val sql = clazz.deleteTableSQL
        runSQL(sql) { statement -> statement.execute() }
    }

    override fun <K : Any> deleteAllData(clazz: MunchClass<K, *>) {
        val sql = clazz.deleteAllSQL
        runSQL(sql) { statement -> statement.execute() }
    }

    override fun <K : Any, V : Any> deleteData(clazz: MunchClass<K, V>, value: V) {
        val sql = clazz.deleteSQL
        runSQL(sql) { statement ->
            setValue(statement, 1, value)
            statement.execute()
        }
    }

    override fun <K : Any, V : Any> updateData(clazz: MunchClass<K, V>, obj: K, value: V) {
        val sql = clazz.updateSQL
        runSQL(sql) { statement ->
            val columns = clazz.columns.keys.toList()

            val currentIndex = addKotlinValues(statement, obj, columns)
            setValue(statement, currentIndex, value)

            statement.execute()
        }
    }

    override fun <K : Any> updateAllData(clazz: MunchClass<K, *>, obj: Array<K>) =
        updateAllData(clazz, obj.toList())

    override fun <K : Any> updateAllData(clazz: MunchClass<K, *>, obj: List<K>) {
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
