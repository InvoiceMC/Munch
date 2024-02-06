package me.outspending.munch.serializer

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
}
