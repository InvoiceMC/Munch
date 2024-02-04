package me.outspending.connection

import java.io.File
import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager

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
    fun connect(databaseName: String) = connect(File(databaseName))

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
     * Checks if the connection is closed.
     *
     * @return If the connection is closed.
     * @since 1.0.0
     * @since 1.0.0
     */
    fun hasConnection(): Boolean = connection.isClosed
}
