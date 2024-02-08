package me.outspending.munch.connection

import me.outspending.munch.MunchClass
import me.outspending.munch.serializer.SerializerFactory
import java.io.File
import java.io.IOException
import java.lang.reflect.Field
import java.sql.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.javaField

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

        /**
         * This method does the same thing as [create] but it will also connect to the database and
         * create the table for you without needing to call [connect] and [createTable]. This is
         * mostly a QOL method.
         *
         * @param clazz The [MunchClass] instance to be used.
         * @return A new instance of the [MunchConnection] interface.
         * @see MunchConnection
         * @see MunchClass
         * @author Outspending
         * @since 1.0.0
         */
        fun <T : Any, K : Any> create(clazz: MunchClass<T, K>) =
            MunchDatabase().apply {
                connect()
                createTable(clazz)
            }
    }

    /**
     * This method is used to connect to the SQLite database.
     *
     * @param databaseName The name of the database.
     * @throws IOException If the file cannot be created.
     * @author Outspending
     * @since 1.0.0
     */
    fun connect(databaseName: String = "database.db", runAsync: Boolean = false)

    /**
     * This method is used to connect to the SQLite database.
     *
     * @param parentPath The parent path of the database.
     * @param databaseName The name of the database.
     * @throws IOException If the file cannot be created.
     * @author Outspending
     * @since 1.0.0
     */
    fun connect(parentPath: File, databaseName: String, runAsync: Boolean = false)

    /**
     * This method is used to connect to the SQLite database.
     *
     * @param parentPath The parent path of the database.
     * @param databaseName The name of the database.
     * @throws IOException If the file cannot be created.
     * @author Outspending
     * @since 1.0.0
     */
    fun connect(file: File, runAsync: Boolean = false)

    /**
     * This method will disconnect the database from Munch. This can be useful if you want to close
     * the connection to the database.
     *
     * @see MunchConnection
     * @see MunchDatabase
     * @author Outspending
     * @since 1.0.0
     */
    fun disconnect()

    /**
     * This method is used to check if the connection is connected to the database.
     *
     * @return If the connection is connected to the database.
     * @see MunchConnection
     * @see MunchDatabase
     * @since 1.0.0
     */
    fun isConnected(): Boolean

    /**
     * This method is used to run custom SQL without any generators.
     *
     * @param sql The SQL to be executed.
     * @param execute The lambda to execute the SQL.
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any> runSQL(sql: String, execute: (PreparedStatement) -> T?): T?

    /**
     * This method is used to create a table in the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any, K : Any> createTable(clazz: MunchClass<T, K>, runAsync: Boolean = false)

    /**
     * This method gets all the data from the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @return A [ResultSet] of all the data from the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any, K : Any> getAllData(clazz: MunchClass<T, K>, runAsync: Boolean = false): List<T>?

    /**
     * This method is used to check if the data exists in the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param value The value to be checked.
     * @return If the data exists in the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any, K : Any> hasData(
        clazz: MunchClass<T, K>,
        value: K,
        runAsync: Boolean = false
    ): Boolean?

    /**
     * This method is used to insert data into the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be inserted into the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any, K : Any> addData(clazz: MunchClass<T, K>, obj: T, runAsync: Boolean = false)

    /**
     * This method is used to insert data into the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be inserted into the database.
     * @throws SQLException If the data cannot be inserted.
     * @see SQLException
     * @see MunchClass
     * @since 1.0.0
     */
    fun <T : Any, K : Any> addAllData(
        clazz: MunchClass<T, K>,
        obj: Array<T>,
        runAsync: Boolean = false
    )

    /**
     * This method is used to insert data into the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be inserted into the database.
     * @throws SQLException If the data cannot be inserted.
     * @see SQLException
     * @see MunchClass
     * @since 1.0.0
     */
    fun <T : Any, K : Any> addAllData(
        clazz: MunchClass<T, K>,
        obj: List<T>,
        runAsync: Boolean = false
    )

    /**
     * This method is used to insert data into the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be inserted into the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any, K : Any> getData(clazz: MunchClass<T, K>, value: K, runAsync: Boolean = false): T?

    /**
     * This method is used to delete the whole table inside the database. This is useful for
     * clearing the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @throws SQLException If the table cannot be deleted.
     * @see SQLException
     * @see MunchClass
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any, K : Any> deleteTable(clazz: MunchClass<T, K>, runAsync: Boolean = false)

    /**
     * This method is used to delete all the data from the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @throws SQLException If the data cannot be deleted.
     * @see SQLException
     * @see MunchClass
     * @since 1.0.0
     */
    fun <T : Any, K : Any> deleteAllData(clazz: MunchClass<T, K>, runAsync: Boolean = false)

    /**
     * This method is used to delete data from the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param value The value to be deleted from the database.
     * @throws SQLException If the data cannot be deleted.
     * @see SQLException
     * @see MunchClass
     * @since 1.0.0
     */
    fun <T : Any, K : Any> deleteData(clazz: MunchClass<T, K>, value: K, runAsync: Boolean = false)

    /**
     * This method is used to update data in the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be updated in the database.
     * @throws SQLException If the data cannot be updated.
     * @see SQLException
     * @see MunchClass
     * @since 1.0.0
     */
    fun <T : Any, K : Any> updateData(
        clazz: MunchClass<T, K>,
        obj: T,
        key: K,
        runAsync: Boolean = false
    )

    /**
     * This method is used to update data in the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be updated in the database.
     * @throws SQLException If the data cannot be updated.
     * @see SQLException
     * @see MunchClass
     * @since 1.0.0
     */
    fun <T : Any, K : Any> updateAllData(
        clazz: MunchClass<T, K>,
        obj: Array<T>,
        runAsync: Boolean = false
    )

    /**
     * This method is used to update data in the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be updated in the database.
     * @throws SQLException If the data cannot be updated.
     * @see SQLException
     * @see MunchClass
     * @since 1.0.0
     */
    fun <T : Any, K : Any> updateAllData(
        clazz: MunchClass<T, K>,
        obj: List<T>,
        runAsync: Boolean = false
    )

    fun addValue(statement: PreparedStatement, obj: Any): Int {
        val fields = obj::class.java.declaredFields
        addValues(statement, obj, fields.toList())

        return fields.size
    }

    fun <T> addKotlinValues(
        statement: PreparedStatement,
        obj: Any,
        fields: List<KProperty1<out T, *>>
    ) = addValues(statement, obj, fields.map { it.javaField!! })

    fun addValues(statement: PreparedStatement, obj: Any, fields: List<Field>): Int {
        for ((index, field) in fields.withIndex()) {
            field.isAccessible = true

            val value = field.get(obj)
            value?.let { setValue(statement, index + 1, it) }
        }

        return fields.size + 1
    }

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
