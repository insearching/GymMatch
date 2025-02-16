package com.insearching.urbansports.gyms.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.domain.model.Gym

@Entity(tableName = "gym")
data class GymEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    @ColumnInfo(name = "address")
    val address: String,
    @ColumnInfo(name = "communityCenter")
    val communityCenter: String,
    @ColumnInfo(name = "facilityTitle")
    val facilityTitle: String,
    @ColumnInfo(name = "group")
    val group: String?,
    @ColumnInfo(name = "location")
    val location: String,
    @ColumnInfo(name = "openGym")
    val openGym: String,
    @ColumnInfo(name = "openGymEnd")
    val openGymEnd: String,
    @ColumnInfo(name = "openGymStart")
    val openGymStart: String,
    @ColumnInfo(name = "passType")
    val passType: String,
    @ColumnInfo(name = "postalCode")
    val postalCode: String,
    @ColumnInfo(name = "provinceCode")
    val provinceCode: String,
    @ColumnInfo(name = "total")
    val total: Int?,
    @ColumnInfo(name = "totalFemales")
    val totalFemales: Int?,
    @ColumnInfo(name = "totalMales")
    val totalMales: Int?,
    @ColumnInfo(name = "totalNonResidents")
    val totalNonResidents: Int?,
    @ColumnInfo(name = "totalResidents")
    val totalResidents: Int?,
    @ColumnInfo(name = "geoPoint")
    val geoPoint: GeoPoint? = null,
    @ColumnInfo(name = "isFavorite")
    val isFavorite: Boolean = false,
    @ColumnInfo(name = "isSkipped")
    val isSkipped: Boolean = false
)