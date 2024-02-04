package me.outspending.connection

import me.outspending.MunchClass
import me.outspending.generator.generateSelect
import me.outspending.generator.generateSelectAll
import me.outspending.generator.generateTable
import me.outspending.serializer.Serializer
import me.outspending.serializer.SerializerFactory
import java.io.File
import java.io.IOException
import java.sql.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.memberProperties
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
        val sql = clazz.generateSelectAll()

        try {
            val statement = connection.prepareStatement(sql)

            return statement.executeQuery()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return null
    }

    /**
     * This method is used to insert data into the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be inserted into the database.
     * @since 1.0.0
     */
    fun <T : Any> getData(clazz: MunchClass<T>, value: Any): List<T>? {
        val sql = clazz.generateSelect()

        try {
            val statement = connection.prepareStatement(sql)
            statement.setString(1, value.toString())

            val resultSet = statement.executeQuery()

            val data = mutableListOf<T>()

            while (resultSet.next()) {
                val obj = clazz::class.createInstance()

                for (property in clazz::class.memberProperties) {
                    if (property !is KMutableProperty1<*, *>) {
                        continue
                    }

                    val setValue: (Any?) -> Unit = { value ->
                        property.isAccessible = true
                        property.setter.call(obj, value)
                    }

                    when (property.returnType.classifier) {
                        String::class -> setValue(resultSet.getString(property.name))
                        Int::class -> setValue(resultSet.getInt(property.name))
                        Long::class -> setValue(resultSet.getLong(property.name))
                        Double::class -> setValue(resultSet.getDouble(property.name))
                        Float::class -> setValue(resultSet.getFloat(property.name))
                        Boolean::class -> setValue(resultSet.getBoolean(property.name))
                        else -> {
                            val serializer = SerializerFactory.getSerializer(property.returnType.classifier as KClass<*>)

                            if (serializer != null) {
                                val serializedValue = resultSet.getString(property.name)
                                val deserialized = serializer.deserialize(serializedValue)

                                setValue(deserialized)
                            }
                        }
                    }
                }

                data.add(obj as T)
            }

            return data
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return null
    }

}
