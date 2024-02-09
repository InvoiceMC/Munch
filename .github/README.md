# 🍽️ Munch
Munch is an `open-source` SQLite annotation API. This API has great performance and has frequent updates. 

Munch offers many systems to improve your productivity such as offering `Serializers, Custom Types, and SQL Generators`. You can create a fully functional database in **~10 Lines**!

> [!NOTE]
> Munch is currently in the **BETA** stage, we are currently foucused on improving memory usage / performance.
 
## 🤔 Why Munch?
Munch is a very optimized API which offers many things at your disposal, which has reduced the amount of code you need to write by about `93.33%` less code!

- Using Default SQLite: `~150 lines`
- Using Munch: `~10 lines`

Munch also offers lots of features you can use such as
- Thread Safe
- Low Performance Cost
- User-Friendly

## ⭐ Adding Munch to Your Project
Check out our jitpack to add **Munch** to your project [HERE](https://jitpack.io/#InvoiceMC/Munch)

## 🎉 Creating Database
Firstly you will need to create your `data class`. This class has to have the `@Table` annotation or the processor wont work. This class will also **HAVE** to be a **data** class.

You will also need to make all the variables **var** since internally we are casting the properties into Mutable thus Munch will not work with **val** values.

Here's an example of creating that data class:
```kt
@Table("TableName") // If the table name isn't set it will default to the data class's name
data class PlayerData(
    @PrimaryKey var id: Int,
    @Column var name: String,
    @Column(constraints = [ColumnConstraint.NOTNULL]) var level: Int, // This will make the value NOT NULL in the SQLite database
    @Column(ColumnType.BOOLEAN) var isCool: Boolean, // Column types are types that are offered by SQLite to improve storage. If the column type isn't set it will still work just will use more storage
) : Data {
    constructor() : this(0, "", false) // An empty constructor is REQUIRED for Munch to work properly
}
```

Then to make a database for it you will need to do this:
```kt
fun main() {
    val clazz = Munch.create(PlayerData::class).process<Int>() // <Int> is the primary key's type, which just helps with auto completion using the connection
    val database = MunchConnection.create(clazz) // This will auto create the database for you if you dont want to create a table and connect to the database you can use `MunchConnection.create()`

    val data = PlayerData(10, "Insavings", true)
    database.addData(clazz, data)
}
```

 ## 🧑‍💻 Munch Serialization
 Munch offers `Serialization` for your types. This can easily be created by doing:
```kt
import me.outspending.munch.serializer.Serializer
import java.util.*

class UUIDSerializer : Serializer<UUID> {
    override fun getSerializerClass(): Class<UUID> = UUID::class.java

    override fun deserialize(str: String): UUID = UUID.fromString(str)

    override fun serialize(obj: Any?): String = (obj as UUID).toString()
}
```
You will also need to register that serializer by doing:
```kt
SerializerFactory.registerSerializer(UUIDSerializer())
// OR
SerializerFactory.registerSerializers("package.name")
```

## Contributers
<a href="https://github.com/InvoiceMC/Munch/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=InvoiceMC/Munch" />
</a>
