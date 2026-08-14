---
name: kmp-starter-feature-database
description: The KMP Starter Template Room database — entities, DAOs, migrations, and DB configuration in features/database. How to add tables and bump versions safely.
author: DevAtrii
license: MIT

---

# Database (Room)

Room Database for local storage with multiplatform support. Entities, DAOs, migrations. Single module: `features/database`.

## Where things live

| Piece | Path |
| --- | --- |
| `KmpStarterDatabase` (abstract Room db) | `features/database/src/commonMain/.../KmpStarterDatabase.kt` |
| `KmpStarterDatabaseMigrations` | `features/database/src/commonMain/.../KmpStarterDatabaseMigrations.kt` |
| `getKmpDatabase` (builder) | `features/database/src/commonMain/.../DatabaseProvider.kt` |
| DI | `features/database/src/commonMain/.../di/DatabaseModule.kt` |
| `KmpStarterDatabaseConstructor` | platform `expect`/`actual` (Android/iOS) |

## 1. Configure the database

`KmpStarterDatabase` defines the DB name and version:

```kotlin
@Database(
    entities = [SampleEntity::class],
    version = KmpStarterDatabase.DB_VERSION,
    exportSchema = false,
)
@ConstructedBy(KmpStarterDatabaseConstructor::class)
abstract class KmpStarterDatabase : RoomDatabase() {
    companion object {
        const val DB_NAME = "kmp_starter.db"
        const val DB_VERSION = 1   // increment on any schema change
    }
    abstract fun myDao(): MyDao
}
```

The builder (`getKmpDatabase`) wires migrations, driver, and IO context:

```kotlin
fun getKmpDatabase(...): KmpStarterDatabase {
    val builder = ...
    return builder
        .addMigrations(*KmpStarterDatabaseMigrations.SUPPORTED_MIGRATIONS)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}
```

## 2. Add a table

1. **Entity** — one class per table:

```kotlin
@Entity
data class SampleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: String,
)
```

2. **DAO** — queries, inserts, updates, deletes:

```kotlin
@Dao
interface MyDao {
    @Query("SELECT * FROM sample_entity")
    fun getAll(): Flow<List<SampleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(sampleEntity: SampleEntity): Long
}
```

3. **Register** in `@Database(entities = [...])` and expose the DAO via an abstract fun.

## 3. Migrations

When the schema changes, add a `Migration` and append it to `SUPPORTED_MIGRATIONS` (never mutate an existing migration — add a new one):

```kotlin
object KmpStarterDatabaseMigrations {
    private val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("ALTER TABLE notes ADD COLUMN tags TEXT")
        }
    }
    val SUPPORTED_MIGRATIONS: Array<out Migration> = arrayOf(MIGRATION_1_2)
}
```

Then bump `DB_VERSION` to match the migration's end version.

## Rules

- Always increment `DB_VERSION` when adding columns/tables; add a matching migration.
- Use `TypeConverters` for non-primitive fields (lists, custom objects).
- Register `databaseModule` in `InitKoin` (see koin skill).

## Reference

- Docs: `https://starter.atherio.dev/features/` → Database
- Source: `features/database/*`
