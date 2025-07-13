package com.kmpstarter.core.db

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Entity
data class SampleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: String,
)

@Dao
interface MyDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrency(sampleEntity: SampleEntity): Long

}

@Database(
    entities = [SampleEntity::class],
    version = KmpStarterDatabase.DB_VERSION,
    exportSchema = false
)
//@TypeConverters(/* ..Todo add type converters here.. */)
@ConstructedBy(KmpStarterDatabaseConstructor::class)
abstract class KmpStarterDatabase : RoomDatabase() {

    companion object {
        const val DB_NAME = "kmp_starter.db"
        const val DB_VERSION = 1
    }

    /*Todo add your DAOs here*/
    abstract fun myDao(): MyDao


}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object KmpStarterDatabaseConstructor : RoomDatabaseConstructor<KmpStarterDatabase> {
    override fun initialize(): KmpStarterDatabase
}


















