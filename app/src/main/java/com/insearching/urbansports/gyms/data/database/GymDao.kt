package com.insearching.urbansports.gyms.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.insearching.urbansports.gyms.domain.model.Gym
import kotlinx.coroutines.flow.Flow

@Dao
interface GymDao {

    @Query("SELECT * FROM gym")
    suspend fun getAllGyms(): List<GymEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGyms(note: List<GymEntity>)

    @Update
    suspend fun updateGym(gym: GymEntity)
}
