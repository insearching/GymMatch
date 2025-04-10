package com.insearching.urbansports.gyms.data.mapper

import com.insearching.urbansports.gyms.data.database.GymEntity
import com.insearching.urbansports.gyms.data.networking.dto.GymDto
import com.insearching.urbansports.gyms.domain.model.Gym

fun GymDto.toGym(): Gym {
    return Gym(
        address = address,
        communityCenter = communityCenter,
        facilityTitle = facilityTitle,
        group = group,
        location = location,
        openGym = openGym,
        openGymEnd = openGymEnd,
        openGymStart = openGymStart,
        passType = passType,
        postalCode = postalCode,
        provinceCode = provinceCode,
        total = total,
        totalFemales = totalFemales,
        totalMales = totalMales,
        totalNonResidents = totalNonResidents,
        totalResidents = totalResidents
    )
}

fun GymEntity.toGym() = Gym(
    address = address,
    communityCenter = communityCenter,
    facilityTitle = facilityTitle,
    group = group,
    location = location,
    openGym = openGym,
    openGymEnd = openGymEnd,
    openGymStart = openGymStart,
    passType = passType,
    postalCode = postalCode,
    provinceCode = provinceCode,
    total = total,
    totalFemales = totalFemales,
    totalMales = totalMales,
    totalNonResidents = totalNonResidents,
    totalResidents = totalResidents,
    geoPoint = geoPoint,
    isFavorite = isFavorite,
    isSkipped = isSkipped
)

fun Gym.toEntity() = GymEntity(
    address = address,
    communityCenter = communityCenter,
    facilityTitle = facilityTitle,
    group = group,
    location = location,
    openGym = openGym,
    openGymEnd = openGymEnd,
    openGymStart = openGymStart,
    passType = passType,
    postalCode = postalCode,
    provinceCode = provinceCode,
    total = total,
    totalFemales = totalFemales,
    totalMales = totalMales,
    totalNonResidents = totalNonResidents,
    totalResidents = totalResidents,
    geoPoint = geoPoint,
    isFavorite = isFavorite,
    isSkipped = isSkipped
)
