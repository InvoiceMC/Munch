package me.outspending

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class SQLiteTable

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
annotation class PrimaryKey(val autoIncrement: Boolean = false)

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
annotation class Index

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
annotation class Column(
    val name: String = "",
    val type: ColumnType = ColumnType.TEXT,
    val constraints: Array<ColumnConstraint> = []
)