package me.outspending.munch.serializer

import kotlin.reflect.KClass

/**
 * This is the serializer interface. This is used to serialize and deserialize objects.
 *
 * @param T The type of the object that you want to serialize and deserialize.
 * @constructor Create empty Serializer
 * @author Outspending
 * @since 1.0.0
 */
interface Serializer<T> {
    /**
     * This method is used to get the class of the object that you want to serialize and
     * deserialize.
     *
     * @return The class of the object that you want to serialize and deserialize.
     * @see Class
     * @see T
     * @see Serializer
     * @author Outspending
     * @since 1.0.0
     * @since 1.0.0
     */
    fun getSerializerClass(): Class<T>

    /**
     * This method is used to serialize an object.
     *
     * @param obj The object to be serialized.
     * @return The serialized object.
     * @see T
     * @see Serializer
     * @author Outspending
     * @since 1.0.0
     */
    fun serialize(obj: Any?): String

    /**
     * This method is used to deserialize a string.
     *
     * @param str The string to be deserialized.
     * @return The deserialized object.
     * @see T
     * @see Serializer
     * @author Outspending
     * @since 1.0.0
     */
    fun deserialize(str: String): T

    /**
     * This method is to grab a serializer with the specified class.
     *
     * This value can be null and wont throw an error. If you want to grab a serializer without it
     * being null use [grabSerializer]. Which will throw [IllegalArgumentException] if the
     * serializer is null.
     *
     * @param clazz The serializer's class
     * @return The Serializer (Can be null)
     * @author Outspending
     * @since 1.0.0
     */
    fun <V : Any> getSerializer(clazz: KClass<V>): Serializer<V>? =
        SerializerFactory.getSerializer(clazz)

    /**
     * This method is used to grab a serializer with the specified class.
     *
     * This method does throw an [IllegalArgumentException] if the serializer is null. But can
     * decrease the amount of code you have to write.
     *
     * @param clazz The class of the serializer
     * @return The Serializer
     * @throws IllegalArgumentException If the serializer is null
     * @author Outspending
     * @since 1.0.0
     */
    fun <V : Any> grabSerializer(clazz: KClass<V>): Serializer<V> =
        getSerializer(clazz) ?: throw IllegalArgumentException("No serializer found for $clazz")

    /**
     * This method is used to serialize an object. This method calls [grabSerializer] therefore can
     * throw [IllegalArgumentException].
     *
     * @param clazz The class of the Serializer
     * @param obj The object to be serialized
     * @throws IllegalArgumentException If the serializer is null
     * @author Outspending
     * @since 1.0.0
     */
    fun <V : Any> serializeObject(clazz: KClass<V>, obj: V): String =
        grabSerializer(clazz).serialize(obj)

    /**
     * This method is used to deserialize an object. THis method calls [grabSerializer] therefore
     * can throw [IllegalArgumentException]
     *
     * @param clazz The class of the serializer
     * @param string The String That you want to deserialize
     * @throws IllegalArgumentException If the serializer is null
     * @author Outspending
     * @since 1.0.0
     */
    fun <V : Any> deserializerObject(clazz: KClass<V>, data: String): V =
        grabSerializer(clazz).deserialize(data)
}
