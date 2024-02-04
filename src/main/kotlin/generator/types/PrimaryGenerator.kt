package me.outspending.generator.types

interface PrimaryGenerator<T : Any> {
    /**
     * This method is to handle the primary key of the data class. Which is passed in to the
     * [MunchClass] instance.
     *
     * If you need a reference to the primary key, you can use the [TableGenerator.handlePrimaryKey]
     * property.
     *
     * @author Outspending
     * @since 1.0.0
     */
    fun handlePrimaryKey()
}