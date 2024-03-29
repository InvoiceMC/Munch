package me.outspending.munch.connection

import me.outspending.munch.MunchClass
import java.io.*
import java.lang.reflect.Field
import java.sql.*
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.*
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
         * This method does the same thing as [create] but it will also connect to the database and
         * create the table for you without needing to call [connect] and [createTable]. This is
         * mostly a QOL method.
         *
         * @param clazz The [MunchClass] instance to be used.
         * @return A new instance of the [MunchDatabase] interface.
         * @see MunchClass
         * @author Outspending
         * @since 1.0.0
         */
        fun <K : Any, V : Any> create(clazz: MunchClass<K, V>) = MunchDatabase(clazz)

        /**
         * This method creates a new instance of the [MunchConnection] interface. This class is for
         * executing any [MunchClass] instance.
         *
         * If you are looking to execute a single [MunchClass] instance, you can use the [create]
         * method instead. Which will automatically input the MunchClass for you
         *
         * @return A new instance of the [MunchConnection] interface.
         * @see MunchConnection
         * @see MunchClass
         * @author Outspending
         * @since 1.0.0
         */
        fun global() = GlobalDatabase.getInstance()
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
     * This method is used to run custom getSQL without any generators.
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
    fun <K : Any> createTable(clazz: MunchClass<K, *>)

    /**
     * This method gets all the data from the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @return A [ResultSet] of all the data from the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <K : Any> getAllData(clazz: MunchClass<K, *>): MutableList<K>?

    /**
     * This method is used to check if the data exists in the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param value The value to be checked.
     * @return If the data exists in the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <K : Any, V : Any> hasData(clazz: MunchClass<K, V>, value: V): Boolean

    /**
     * This method is used to insert data into the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be inserted into the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <K : Any> addData(clazz: MunchClass<K, *>, obj: K)

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
    fun <K : Any> addAllData(clazz: MunchClass<K, *>, obj: Array<K>)

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
    fun <K : Any> addAllData(clazz: MunchClass<K, *>, obj: List<K>)

    /**
     * This method is used to insert data into the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param obj The object to be inserted into the database.
     * @author Outspending
     * @since 1.0.0
     */
    fun <K : Any, V : Any> getData(clazz: MunchClass<K, V>, value: V): K?

    /**
     * This method is used to get all the data from the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param value The value to be checked.
     * @return If the data exists in the database.
     * @throws SQLException If the data cannot be inserted.
     * @see SQLException
     * @see MunchClass
     * @since 1.0.0
     */
    fun <K : Any, V : Any> getAllData(clazz: MunchClass<K, V>, value: V): MutableList<K>?

    /**
     * This method is used to get all the data from the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @param filter The filter to be used.
     * @return If the data exists in the database.
     * @throws SQLException If the data cannot be inserted.
     * @see SQLException
     * @see MunchClass
     * @since 1.0.0
     */
    fun <K : Any, V : Any> getAllDataWithFilter(
        clazz: MunchClass<K, V>,
        filter: (K) -> Boolean
    ): List<K>?

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
    fun <K : Any> deleteTable(clazz: MunchClass<K, *>)

    /**
     * This method is used to delete all the data from the database.
     *
     * @param clazz The [MunchClass] instance to be used.
     * @throws SQLException If the data cannot be deleted.
     * @see SQLException
     * @see MunchClass
     * @since 1.0.0
     */
    fun <K : Any> deleteAllData(clazz: MunchClass<K, *>)

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
    fun <K : Any, V : Any> deleteData(clazz: MunchClass<K, V>, value: V)

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
    fun <K : Any, V : Any> updateData(clazz: MunchClass<K, V>, obj: K, value: V)

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
    fun <K : Any> updateAllData(clazz: MunchClass<K, *>, obj: Array<K>)

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
    fun <K : Any> updateAllData(clazz: MunchClass<K, *>, obj: List<K>)
}
