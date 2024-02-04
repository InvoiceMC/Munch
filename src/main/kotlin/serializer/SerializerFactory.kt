package me.outspending.serializer

import org.reflections.Reflections
import kotlin.reflect.KClass

/**
 * This is the serializer factory. This is used to register and get serializers.
 *
 * @constructor Create empty Serializer factory
 * @see Serializer
 *
 * @since 1.0.0
 * @author Outspending
 */
object SerializerFactory {
    private val serializers: MutableMap<Class<*>, Serializer<*>> = mutableMapOf()

    /**
     * This method is used to register serializers.
     *
     * @param serializers The serializers to be registered.
     * @see Serializer
     * @since 1.0.0
     * @author Outspending
     */
    fun registerSerializers(vararg serializers: Serializer<*>) =
        serializers.forEach { registerSerializer(it) }

    /**
     * This method is used to register serializers.
     *
     * @param subPackage The sub package of the serializers.
     * @param packageName The package of the serializers.
     * @see Serializer
     * @since 1.0.0
     * @author Outspending
     */
    fun registerSerializers(subPackage: String, packageName: String) = registerSerializers("$packageName.$subPackage")

    /**
     * This method is used to register serializers.
     *
     * @param packageName The package of the serializers.
     * @see Serializer
     *
     * @since 1.0.0
     * @author Outspending
     */
    fun registerSerializers(packageName: String) =
        Reflections(packageName)
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
     *
     * @see Serializer
     *
     * @since 1.0.0
     * @author Outspending
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> getSerializer(clazz: KClass<T>): Serializer<T>? =
        if (hasSerializer(clazz)) serializers[clazz.java] as Serializer<T>
        else null

    /**
     * This method is used to check if a serializer exists.
     *
     * @param clazz The class of the object that you want to serialize and deserialize.
     * @return True if the serializer exists, false otherwise.
     * @see Serializer
     * @since 1.0.0
     * @author Outspending
     */
    private fun hasSerializer(clazz: KClass<*>): Boolean = clazz.java in serializers

    /**
     * This method is used to register a serializer.
     *
     * @param serializer The serializer to be registered.
     * @see Serializer
     * @since 1.0.0
     * @author Outspending
     */
    private fun <T> registerSerializer(serializer: Serializer<T>) {
        val clazz = serializer.getSerializerClass()
        require(clazz !in serializers) { "Serializer for class ${clazz.name} already exists" }

        serializers[clazz] = serializer
    }
}