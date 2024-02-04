package me.outspending.connection

import me.outspending.MunchClass
import me.outspending.generator.generateInsert
import me.outspending.generator.generateSelect
import me.outspending.generator.generateSelectAll
import me.outspending.generator.generateTable
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

    fun <T : Any> hasData(clazz: MunchClass<T>, value: Any): Boolean {
        val sql = clazz.generateSelect()

        try {
            val statement = connection.prepareStatement(sql)
            statement.setString(1, value.toString())

            val resultSet = statement.executeQuery()
            return resultSet.next()
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return false
    }

    fun <T : Any> addData(clazz: MunchClass<T>, obj: T) {
        val sql = clazz.generateInsert()

        try {
            val statement = connection.prepareStatement(sql)

            for ((index, property) in obj::class.memberProperties.withIndex()) {
                val value = property.getter.call(obj)

                println("VALUE: $value // INDEX: ${index + 1}")
                when (property.returnType.classifier) {
                    String::class -> statement.setString(index + 1, value as String)
                    Int::class -> statement.setInt(index + 1, value as Int)
                    Long::class -> statement.setLong(index + 1, value as Long)
                    Double::class -> statement.setDouble(index + 1, value as Double)
                    Float::class -> statement.setFloat(index + 1, value as Float)
                    Boolean::class -> statement.setBoolean(index + 1, value as Boolean)
                    else -> {
                        println("SERIALIZER")
                        val serializer = SerializerFactory.getSerializer(property.returnType.classifier as KClass<*>)

                        if (serializer != null) {
                            val serializedValue = serializer.serialize(value)
                            statement.setString(index + 1, serializedValue)
                        }
                    }
                }
            }

            statement.executeUpdate()
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    /**
     * This method is used to insert data into the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be inserted into the database.
     * @since 1.0.0
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getData(clazz: MunchClass<T>, value: Any): T? {
        val sql = clazz.generateSelect()

        try {
            println(sql)
            val statement = connection.prepareStatement(sql)
            setValue(statement, 1, value)

            val resultSet = statement.executeQuery()
            if (!resultSet.next()) return null

            println(resultSet)
            val obj = clazz.clazz.createInstance()
            for (property in obj::class.memberProperties) {
                val mutableProperty = (property as? KMutableProperty1<T, *>) ?: continue
                println(mutableProperty)

                val setValue: (Any?) -> Unit = { newValue ->
                    println("SETTING VALUE: $newValue")

                    mutableProperty.isAccessible = true
                    mutableProperty.setter.call(obj, newValue)
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
                        serializer?.let {
                            val serializedValue = resultSet.getString(property.name)
                            val deserialized = serializer.deserialize(serializedValue)

                            setValue(deserialized)
                        }
                    }
                }
            }

            return obj
        } catch (e: SQLException) {
            e.printStackTrace()
        }

        return null
    }

    private fun setValue(statement: PreparedStatement, index: Int, value: Any) {
        when (val clazz = value::class) {
            String::class -> statement.setString(index, value as String)
            Int::class -> statement.setInt(index, value as Int)
            Long::class -> statement.setLong(index, value as Long)
            Double::class -> statement.setDouble(index, value as Double)
            Float::class -> statement.setFloat(index, value as Float)
            Boolean::class -> statement.setBoolean(index, value as Boolean)
            else -> {
                println("SERIALIZER")
                val serializer = SerializerFactory.getSerializer(clazz as KClass<*>)

                if (serializer != null) {
                    val serializedValue = serializer.serialize(value)
                    statement.setString(index, serializedValue)
                }
            }
        }
    }

}
