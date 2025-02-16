package com.insearching.urbansports.gyms.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [GymEntity::class], version = 1)
@TypeConverters(
    GeoPointStringConverter::class
)
abstract class GymDatabase : RoomDatabase() {
    abstract fun gymDao(): GymDao

    companion object {
        const val DB_NAME = "gym.db"
    }
}