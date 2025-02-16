package com.insearching.urbansports.gyms.data.networking.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GymResponseDto(
    @SerialName("results")
    val gyms: List<GymDto>
)

@Serializable
data class GymDto(
    @SerialName("address11")
    val address: String,
    @SerialName("community_center")
    val communityCenter: String,
    @SerialName("facility_title")
    val facilityTitle: String,
    val group: String?,
    val location: String,
    @SerialName("open_gym")
    val openGym: String,
    @SerialName("open_gym_end")
    val openGymEnd: String,
    @SerialName("open_gym_start")
    val openGymStart: String,
    @SerialName("pass_type")
    val passType: String,
    @SerialName("postal_code1")
    val postalCode: String,
    @SerialName("province_code1")
    val provinceCode: String,
    @SerialName("total")
    val total: Int?,
    @SerialName("total_females")
    val totalFemales: Int?,
    @SerialName("total_males")
    val totalMales: Int?,
    @SerialName("total_non_residents")
    val totalNonResidents: Int?,
    @SerialName("total_residents")
    val totalResidents: Int?
)