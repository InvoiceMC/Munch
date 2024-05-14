package me.outspending.benchmarks

import me.outspending.munch.Column
import me.outspending.munch.Munch
import me.outspending.munch.PrimaryKey
import me.outspending.munch.Table
import me.outspending.munch.connection.MunchConnection
import java.util.UUID

class NullTest {
    private val munch = Munch.create(PlayerData::class).process<UUID>()
    private val database = MunchConnection.create(munch)
    private val resource = this::class.java.classLoader.getResource("test.db")

    fun test() {
        try {
            connect()
            val playerData = PlayerData(name = "Jeff")
            database.addData(playerData)
            database.getAllData()?.forEach(::println)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun connect() {
        if (resource == null) {
            throw IllegalStateException("No resources connected")
        }
        database.connect(resource.path)
        database.createTable()
    }

    fun teardown() {
        database.deleteAllData()
    }
}

@Table("playerData")
data class PlayerData(
    @PrimaryKey val uuid: UUID = UUID.randomUUID(),
    @Column val name: String,
    @Column val islandId: UUID? = null,
)