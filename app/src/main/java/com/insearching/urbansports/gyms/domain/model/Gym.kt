package com.insearching.urbansports.gyms.domain.model

data class Gym(
    val address: String,
    val communityCenter: String,
    val facilityTitle: String,
    val group: String?,
    val location: String,
    val openGym: String,
    val openGymEnd: String,
    val openGymStart: String,
    val passType: String,
    val postalCode: String,
    val provinceCode: String,
    val total: Int?,
    val totalFemales: Int?,
    val totalMales: Int?,
    val totalNonResidents: Int?,
    val totalResidents: Int?,
    val distance: Double? = null,
    val geoPoint: GeoPoint? = null,
    val isFavorite: Boolean = false,
    val isSkipped: Boolean = false
) {
    fun getFormattedAddress(): String {
        return "$address, $postalCode"
    }

    fun getSearchAddress(): String {
        return "$postalCode, $provinceCode, $address"
    }
}
