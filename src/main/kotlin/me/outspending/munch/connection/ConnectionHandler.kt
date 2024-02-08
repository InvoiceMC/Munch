package me.outspending.munch.connection

import java.io.File
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

/**
 * This class handles the connection to the database. This is used if there are multiple "datbase"
 * classes that are needing to access this one connection.
 *
 * @author Outspending
 * @since 1.0.0
 */
object ConnectionHandler {
    private lateinit var connection: Connection

    /**
     * This method is used to connect to the database.
     *
     * @param databaseName The name of the database.
     * @param runAsync If the connection should be ran asynchronously.
     * @see File
     * @author Outspending
     * @since 1.0.0
     */
    fun connect(file: File) {
        if (isConnected()) return

        try {
            if (!file.exists()) {
                file.createNewFile()
            }

            val connectionURL = "jdbc:sqlite:${file.absolutePath}"
            connection = DriverManager.getConnection(connectionURL)
        } catch (e: SQLException) {
            e.printStackTrace()
        }
    }

    /**
     * This method is used to disconnect from the database.
     *
     * @author Outspending
     * @since 1.0.0
     */
    fun disconnect() {
        if (!isConnected()) return

        connection.close()
    }

    /**
     * This method is used to check if the connection is open.
     *
     * @return If the connection is open.
     * @author Outspending
     * @since 1.0.0
     */
    fun isConnected(): Boolean = this::connection.isInitialized && !connection.isClosed

    /**
     * This method is used to get the connection.
     *
     * @return The connection.
     * @see Connection
     * @author Outspending
     * @since 1.0.0
     */
    fun getConnection(): Connection = connection
}
