package me.outspending.connection

import java.io.File
import java.io.IOException
import java.sql.*
import kotlin.reflect.jvm.isAccessible
import me.outspending.MunchClass
import me.outspending.generator.generateInsert
import me.outspending.generator.generateSelect
import me.outspending.generator.generateSelectAll
import me.outspending.generator.generateTable

class MunchDatabase : MunchConnection {
    companion object {
        private lateinit var connection: Connection
    }

    override fun connect(databaseName: String) = connect(File(databaseName))

    override fun connect(parentPath: File, databaseName: String) =
        connect(File(parentPath, databaseName))

    override fun connect(file: File) {
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

    override fun runSQL(sql: String, execute: (PreparedStatement) -> Unit) {
        try {
            val statement = connection.prepareStatement(sql)
            execute(statement)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun <T : Any, K : Any> createTable(clazz: MunchClass<T, K>) {
        val sql = clazz.generateTable()

        try {
            val statement = connection.createStatement()

            statement.execute(sql)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    override fun <T : Any, K : Any> getAllData(clazz: MunchClass<T, K>): List<T>? {
        val sql = clazz.generateSelectAll()

        try {
            val statement = connection.prepareStatement(sql)
            val resultSet = statement.executeQuery()

            val list = mutableListOf<T>()
            while (resultSet.next()) {
                generateType(clazz.clazz, resultSet)?.let { list.add(it) }
            }

            return list
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return null
    }

    override fun <T : Any, K : Any> hasData(clazz: MunchClass<T, K>, value: K): Boolean {
        val sql = clazz.generateSelect()

        try {
            val statement = connection.prepareStatement(sql)
            setValue(statement, 1, value)

            return statement.executeQuery().next()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return false
    }

    override fun <T : Any, K : Any> addData(clazz: MunchClass<T, K>, obj: T) {
        val sql = clazz.generateInsert()

        try {
            val statement = connection.prepareStatement(sql)

            for ((index, property) in obj::class.java.declaredFields.withIndex()) {
                property.isAccessible = true

                val value = property.get(obj)
                value?.let { setValue(statement, index + 1, it) }
            }

            statement.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any, K : Any> getData(clazz: MunchClass<T, K>, value: K): T? {
        val sql = clazz.generateSelect()

        try {
            val statement = connection.prepareStatement(sql)
            setValue(statement, 1, value)

            val resultSet = statement.executeQuery()
            if (!resultSet.next()) return null

            return generateType(clazz.clazz, resultSet)
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return null
    }
}
