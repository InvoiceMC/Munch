package me.outspending.munch.serializer

import org.reflections.Reflections
import org.reflections.scanners.Scanners
import org.reflections.util.ClasspathHelper
import org.reflections.util.ConfigurationBuilder
import kotlin.reflect.KClass

/**
 * This is the serializer factory. This is used to register and get serializers.
 *
 * @constructor Create empty Serializer factory
 * @see Serializer
 * @author Outspending
 * @since 1.0.0
 */
object SerializerFactory {
    private val serializers: MutableMap<Class<*>, Serializer<*>> = mutableMapOf()

    /**
     * This method is used to register serializers.
     *
     * @param serializers The serializers to be registered.
     * @see Serializer
     * @author Outspending
     * @since 1.0.0
     */
    fun registerSerializers(vararg serializers: Serializer<*>) =
        serializers.forEach { registerSerializer(it) }

    /**
     * This method is used to register serializers.
     *
     * @param subPackage The sub package of the serializers.
     * @param packageName The package of the serializers.
     * @see Serializer
     * @author Outspending
     * @since 1.0.0
     */
    fun registerSerializers(subPackage: String, packageName: String) =
        registerSerializers("$packageName.$subPackage")

    /**
     * This method is used to register serializers.
     *
     * @param packageName The package of the serializers.
     * @see Serializer
     * @author Outspending
     * @since 1.0.0
     */
    fun registerSerializers(packageName: String) =
        Reflections(
                ConfigurationBuilder()
                    .forPackage(packageName)
                    .setScanners(Scanners.SubTypes)
            )
            .getSubTypesOf(Serializer::class.java)
            .forEach {
                val serializer = it.getDeclaredConstructor().newInstance() as Serializer<*>
                registerSerializer(serializer)
            }

    /**
     * This method is used to get a serializer.
     *
     * @param clazz The class of the object that you want to serialize and deserialize.
     * @return The serializer of the object that you want to serialize and deserialize.
     * @see Serializer
     * @author Outspending
     * @since 1.0.0
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getSerializer(clazz: KClass<T>): Serializer<T>? =
        if (hasSerializer(clazz)) serializers[clazz.java] as Serializer<T> else null

    /**
     * This method is used to serialize an object.
     *
     * @param clazz The class of the object that you want to serialize and deserialize.
     * @param value The object to be serialized.
     * @return The serialized object.
     * @see Serializer
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any> serializeType(clazz: KClass<T>, value: T): String? {
        val serializer = getSerializer(clazz)
        serializer?.let {
            return it.serialize(value)
        }

        return null
    }

    /**
     * This method is used to deserialize a string.
     *
     * @param clazz The class of the object that you want to serialize and deserialize.
     * @param value The string to be deserialized.
     * @return The deserialized object.
     * @see Serializer
     * @author Outspending
     * @since 1.0.0
     */
    fun <T : Any> deserializeType(clazz: KClass<T>, value: String): T? {
        val serializer = getSerializer(clazz)
        serializer?.let {
            return it.deserialize(value)
        }

        return null
    }

    /**
     * This method is used to check if a serializer exists.
     *
     * @param clazz The class of the object that you want to serialize and deserialize.
     * @return True if the serializer exists, false otherwise.
     * @see Serializer
     * @author Outspending
     * @since 1.0.0
     */
    fun hasSerializer(clazz: KClass<*>): Boolean = clazz.java in serializers

    /**
     * This method is used to register a serializer.
     *
     * @param serializer The serializer to be registered.
     * @see Serializer
     * @author Outspending
     * @since 1.0.0
     */
    fun <T> registerSerializer(serializer: Serializer<T>) {
        val clazz = serializer.getSerializerClass()
        require(clazz !in serializers) { "Serializer for class ${clazz.name} already exists" }

        serializers[clazz] = serializer
    }
}
