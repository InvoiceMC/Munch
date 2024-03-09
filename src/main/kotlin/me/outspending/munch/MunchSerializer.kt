package me.outspending.munch

import java.io.*

class MunchSerializer {

    fun <T : Serializable> serialize(obj: T): ByteArray {
        return try {
            val byteStream = ByteArrayOutputStream()
            val objectStream = ObjectOutputStream(byteStream)
            objectStream.writeObject(obj)
            objectStream.close()

            byteStream.toByteArray()
        } catch (e: Exception) {
            throw e
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Serializable> deserialize(bytes: ByteArray): T {
        return try {
            val byteStream = ByteArrayInputStream(bytes)
            val objectStream = ObjectInputStream(byteStream)
            val obj = objectStream.readObject() as T
            objectStream.close()

            obj
        } catch (e: Exception) {
            throw e
        }
    }

}