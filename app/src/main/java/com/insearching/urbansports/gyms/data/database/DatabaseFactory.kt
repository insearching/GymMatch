package com.insearching.urbansports.gyms.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

class DatabaseFactory(
    private val context: Context
) {
    fun create(): RoomDatabase.Builder<GymDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(GymDatabase.DB_NAME)

        return Room.databaseBuilder(
            context = context,
            name = dbFile.absolutePath,
            klass = GymDatabase::class.java
        )
    }
}