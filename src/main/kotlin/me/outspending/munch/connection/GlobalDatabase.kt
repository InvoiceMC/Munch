package me.outspending.munch.connection

import me.outspending.munch.MunchClass
import me.outspending.munch.MunchSerializer
import java.io.*
import java.lang.reflect.Field
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.javaField

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

    private val serializer = MunchSerializer()

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

    private fun addValue(statement: PreparedStatement, obj: Any): Int {
        val fields = obj::class.java.declaredFields
        addValues(statement, obj, fields.toList())

        return fields.size
    }

    private fun <T> addKotlinValues(
        statement: PreparedStatement,
        obj: Any,
        fields: List<KProperty1<out T, *>>
    ) = addValues(statement, obj, fields.map { it.javaField!! })

    private fun addValues(statement: PreparedStatement, obj: Any, fields: List<Field>): Int {
        for ((index, field) in fields.withIndex()) {
            field.isAccessible = true

            val value = field[obj]
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
    private fun setValue(statement: PreparedStatement, index: Int, value: Any) {
        when (value::class) {
            String::class -> statement.setString(index, value as String)
            Int::class -> statement.setInt(index, value as Int)
            Long::class -> statement.setLong(index, value as Long)
            Double::class -> statement.setDouble(index, value as Double)
            Float::class -> statement.setFloat(index, value as Float)
            Boolean::class -> statement.setBoolean(index, value as Boolean)
            else -> {
                if (value is Serializable) {
                    val byteArray = serializer.serialize(value)

                    statement.setBinaryStream(index, ByteArrayInputStream(byteArray), byteArray.size)
                } else {
                    throw IllegalArgumentException("The value cannot be serialized")
                }
            }
        }
    }

    private fun <T : Any> generateType(clazz: KClass<T>, resultSet: ResultSet): T? {
        val constructor = clazz.primaryConstructor ?: return null
        val parameters = constructor.parameters
        val parameterValues =
            parameters.associateWith { parameter ->
                when (parameter.type.classifier) {
                    String::class -> resultSet.getString(parameter.name)
                    Int::class -> resultSet.getInt(parameter.name)
                    Long::class -> resultSet.getLong(parameter.name)
                    Double::class -> resultSet.getDouble(parameter.name)
                    Float::class -> resultSet.getFloat(parameter.name)
                    Boolean::class -> resultSet.getBoolean(parameter.name)
                    else -> {
                        val blob = resultSet.getBytes(parameter.name)

                        if (blob == null) {
                            null
                        } else {
                            serializer.deserialize(blob) as T
                        }
                    }
                }
            }
        return constructor.callBy(parameterValues)
    }
}
