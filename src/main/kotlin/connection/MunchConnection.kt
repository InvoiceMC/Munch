package me.outspending.connection

import me.outspending.MunchClass
import me.outspending.generator.generateTable
import me.outspending.serializer.Serializer
import me.outspending.serializer.SerializerFactory
import java.io.File
import java.io.IOException
import java.sql.*
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance
import kotlin.reflect.jvm.isAccessible

/**
 * This class is used to connect to Munch's SQLite database. You can also use your own connection if
 * you want.
 *
 * @author Outspending
 * @since 1.0.0
 */
class MunchConnection {
    companion object {
        private lateinit var connection: Connection
    }

    /**
     * This method is used to connect to the SQLite database.
     *
     * @param databaseName The name of the database.
     * @throws IOException If the file cannot be created.
     * @since 1.0.0
     */
    fun connect(databaseName: String = "database.db") = connect(File(databaseName))

    /**
     * This method is used to connect to the SQLite database.
     *
     * @param parentPath The parent path of the database.
     * @param databaseName The name of the database.
     * @throws IOException If the file cannot be created.
     * @since 1.0.0
     */
    fun connect(parentPath: File, databaseName: String) = connect(File(parentPath, databaseName))

    /**
     * This method is used to connect to the SQLite database.
     *
     * @param parentPath The parent path of the database.
     * @param databaseName The name of the database.
     * @throws IOException If the file cannot be created.
     * @author Outspending
     * @since 1.0.0
     * @since 1.0.0
     */
    fun connect(file: File) {
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

    /**
     * This method is used to create a table in the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @since 1.0.0
     */
    fun <T : Any> createTable(clazz: MunchClass<T>) {
        val sql = clazz.generateTable()

        try {
            val statement = connection.createStatement()

            statement.execute(sql)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    /**
     * This method gets all the data from the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @return A [ResultSet] of all the data from the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any> getAllData(clazz: MunchClass<T>): ResultSet? {
        val sql = "SELECT * FROM ${clazz.getName()}"

        try {
            val statement = connection.prepareStatement(sql)

            return statement.executeQuery()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * This method is used to get data by the primary key.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param value The value of the primary key.
     * @return A [ResultSet] of the data from the database.
     * @since 1.0.0
     */
    fun <T : Any, K> getDataByPrimary(clazz: MunchClass<T>, value: K): ResultSet? {
        val primaryKey = clazz.primaryKey
        primaryKey?.let {
            val sql = "SELECT * FROM ${clazz.getName()} WHERE ${it.first.name} = ?"

            try {
                val statement = connection.prepareStatement(sql)
                setType(statement, primaryKey.first.returnType.classifier as KClass<*>, value, 1)

                return statement.executeQuery()
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        }

        return null
    }

    /**
     * Checks if the connection is closed.
     *
     * @return If the connection is closed.
     * @since 1.0.0
     * @since 1.0.0
     */
    fun hasConnection(): Boolean = connection.isClosed

    /**
     * This method is used to set the type of the prepared statement.
     *
     * @param statement The prepared statement to be used.
     * @param type The type of the value.
     * @param value The value to be used.
     * @param index The index of the prepared statement.
     * @author Outspending
     * @since 1.0.0
     */
    @Suppress("UNCHECKED_CAST")
    private fun <K> setType(statement: PreparedStatement, type: KClass<*>, value: K, index: Int = 1) {
        return when (type) {
            String::class -> statement.setString(index, value as String)
            Int::class -> statement.setInt(index, value as Int)
            Long::class -> statement.setLong(index, value as Long)
            Short::class -> statement.setShort(index, value as Short)
            Byte::class -> statement.setByte(index, value as Byte)
            Float::class -> statement.setFloat(index, value as Float)
            Double::class -> statement.setDouble(index, value as Double)
            Boolean::class -> statement.setBoolean(index, value as Boolean)
            else -> {
                val serializer: Serializer<K>? = SerializerFactory.getSerializer(type) as? Serializer<K>
                if (serializer != null) {
                    val serialized = serializer.serialize(value)
                    statement.setString(index, serialized)
                } else {
                    throw IllegalArgumentException("The type $type is not supported")
                }
            }
        }
    }
}
