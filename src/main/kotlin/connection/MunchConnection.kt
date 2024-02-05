package me.outspending.connection

import java.io.File
import java.io.IOException
import java.sql.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible
import me.outspending.MunchClass
import me.outspending.serializer.SerializerFactory

/**
 * This class is used to connect to Munch's SQLite database. You can also use your own connection if
 * you want.
 *
 * @author Outspending
 * @since 1.0.0
 */
interface MunchConnection {
    companion object {

        /**
         * This method is used to create a new instance of the [MunchConnection] interface.
         *
         * @return A new instance of the [MunchConnection] interface.
         * @see MunchConnection
         * @author Outspending
         * @since 1.0.0
         */
        fun create(): MunchConnection = MunchDatabase()
    }

    /**
     * This method is used to connect to the SQLite database.
     *
     * @param databaseName The name of the database.
     * @throws IOException If the file cannot be created.
     * @author Outspending
     * @since 1.0.0
     */
    fun connect(databaseName: String = "database.db")

    /**
     * This method is used to connect to the SQLite database.
     *
     * @param parentPath The parent path of the database.
     * @param databaseName The name of the database.
     * @throws IOException If the file cannot be created.
     * @author Outspending
     * @since 1.0.0
     */
    fun connect(parentPath: File, databaseName: String)

    /**
     * This method is used to connect to the SQLite database.
     *
     * @param parentPath The parent path of the database.
     * @param databaseName The name of the database.
     * @throws IOException If the file cannot be created.
     * @author Outspending
     * @since 1.0.0
     */
    fun connect(file: File)

    /**
     * This method is used to run custom SQL without any generators.
     *
     * @param sql The SQL to be executed.
     * @param execute The lambda to execute the SQL.
     * @author Outspending
     * @since 1.0.0
     */
    fun runSQL(sql: String, execute: (PreparedStatement) -> Unit)

    /**
     * This method is used to create a table in the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any, K : Any> createTable(clazz: MunchClass<T, K>)

    /**
     * This method gets all the data from the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @return A [ResultSet] of all the data from the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any, K : Any> getAllData(clazz: MunchClass<T, K>): List<T>?

    /**
     * This method is used to check if the data exists in the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param value The value to be checked.
     * @return If the data exists in the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any, K : Any> hasData(clazz: MunchClass<T, K>, value: K): Boolean

    /**
     * This method is used to insert data into the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be inserted into the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any, K : Any> addData(clazz: MunchClass<T, K>, obj: T)

    /**
     * This method is used to insert data into the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be inserted into the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any, K : Any> getData(clazz: MunchClass<T, K>, value: K): T?

    /**
     * This method is used to close the connection to the database.
     *
     * @author Outspending
     * @since 1.0.0
     */
    fun setValue(statement: PreparedStatement, index: Int, value: Any) {
        when (val clazz = value::class) {
            String::class -> statement.setString(index, value as String)
            Int::class -> statement.setInt(index, value as Int)
            Long::class -> statement.setLong(index, value as Long)
            Double::class -> statement.setDouble(index, value as Double)
            Float::class -> statement.setFloat(index, value as Float)
            Boolean::class -> statement.setBoolean(index, value as Boolean)
            else -> {
                val serializer = SerializerFactory.getSerializer(clazz as KClass<*>)
                serializer?.let {
                    val serializedValue = serializer.serialize(value)

                    statement.setString(index, serializedValue)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Any> generateType(clazz: KClass<T>, resultSet: ResultSet): T? {
        val obj = clazz.createInstance()
        for (property in obj::class.memberProperties) {
            val mutableProperty = (property as? KMutableProperty1<T, *>) ?: continue

            val setValue: (Any?) -> Unit = { newValue ->
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
                    val serializer =
                        SerializerFactory.getSerializer(property.returnType.classifier as KClass<*>)
                    serializer?.let {
                        val serializedValue = resultSet.getString(property.name)
                        val deserialized = serializer.deserialize(serializedValue)

                        setValue(deserialized)
                    }
                }
            }
        }

        return obj
    }
}
