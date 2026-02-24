---

comments: false
icon: lucide/database
---

# Database

Starter Template integrates **Room Database** for local storage with multiplatform support. It provides a clean and structured way to manage your local data with entities, DAOs, and migrations.

---

## Migration

When you update your database schema, you must define migrations to avoid data loss.

```kotlin linenums="1" title="features/database/src/commonMain/.../KmpStarterDatabaseMigrations.kt"  
// Example migration from version 1 to 2
private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(connection: SQLiteConnection) {
        val query = "ALTER TABLE notes ADD COLUMN tags TEXT"
        connection.execSQL(query)
    }
}
```

Add the migration to the supported migrations array:

```kotlin linenums="1" title="features/database/src/commonMain/.../KmpStarterDatabaseMigrations.kt"  
val SUPPORTED_MIGRATIONS: Array<out Migration> = arrayOf(MIGRATION_1_2)
```

??? abstract "Full Example"
    ````kotlin linenums="1" title="features/database/src/commonMain/.../KmpStarterDatabaseMigrations.kt"
    object KmpStarterDatabaseMigrations {

        // Migration from version 1 to 2: adds a new column 'tags' to notes table
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(connection: SQLiteConnection) {
                val createTableQuery = "ALTER TABLE notes ADD COLUMN tags TEXT"
                connection.execSQL(createTableQuery)
            }
        }

        // Include all supported migrations here
        val SUPPORTED_MIGRATIONS: Array<out Migration> = arrayOf(MIGRATION_1_2)

    }
    ````

---

## Database Configuration

Configure your database name and version. This ensures that Room can manage schema updates and create the database file correctly.

```kotlin linenums="1" title="features/database/src/commonMain/.../KmpStarterDatabase.kt"  
...
abstract class KmpStarterDatabase : RoomDatabase() {
    ...
    companion object {
        const val DB_NAME = "kmp_starter.db" // Database file name
        const val DB_VERSION = 1             // Increment when schema changes
    }
    ...
}
...
```

---

## Adding Tables

### Entities

Entities represent your tables. Each entity corresponds to a table in the database.

```kotlin linenums="1" title="features/database/src/commonMain/.../entity/SampleEntity.kt"  
@Entity
data class SampleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,   // Unique ID for each row
    val value: String, // Column storing some value
)
```

### DAO

DAOs define how to interact with your entities. They include queries, inserts, updates, and deletes.

```kotlin linenums="1" title="features/database/src/commonMain/.../dao/SampleDao.kt"  
@Dao
interface SampleDao {

    @Query("SELECT * FROM sample_entity")
    fun getAll(): Flow<List<SampleEntity>> // Observe all rows as a Flow

    @Insert
    suspend fun insert(sampleEntity: SampleEntity) // Insert a new row
}
```

### Add Entity to Database

Register your entity and DAO in `KmpStarterDatabase`. This allows Room to generate the necessary database implementation.

```kotlin linenums="1" title="features/database/src/commonMain/.../KmpStarterDatabase.kt"  
@Database(
    entities = [
        SampleEntity::class  // Add all entities here
    ],
    version = KmpStarterDatabase.DB_VERSION,
    exportSchema = false
)
// @TypeConverters(/* Add converters if needed */)
abstract class KmpStarterDatabase : RoomDatabase() {
    abstract fun sampleDao(): SampleDao // Access DAO through database
}
```

!!! note
    - Add multiple entities by including them in the `entities` array.
    - Use **TypeConverters** if your entities have non-primitive fields like lists or custom objects.
    - Always increment `DB_VERSION` when you add new columns or tables to trigger migrations.

---

## Summary

1. **Define migrations** for schema changes and add them to `SUPPORTED_MIGRATIONS`.
2. **Create entities** in the `entity` package to represent tables.
3. **Define DAOs** in the `dao` package to handle data operations.
4. **Register entities and DAOs** in `KmpStarterDatabase`.
5. **Add TypeConverters** when using non-primitive fields.

This structure ensures your database is maintainable, type-safe, and ready for future schema updates.
