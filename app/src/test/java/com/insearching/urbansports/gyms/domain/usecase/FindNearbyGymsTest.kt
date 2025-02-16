package com.insearching.urbansports.gyms.domain.usecase

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.core.domain.util.errorOrNull
import com.insearching.urbansports.core.domain.util.getOrDefault
import com.insearching.urbansports.core.domain.util.isSucceeded
import com.insearching.urbansports.gyms.domain.GymsRepository
import com.insearching.urbansports.gyms.domain.LocationManager
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.domain.model.Gym
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class FindNearbyGymsTest {
    private val gymsRepository = mockk<GymsRepository>(relaxed = true)
    private val locationManager = mockk<LocationManager>(relaxed = true)

    private lateinit var findNearbyGyms: FindNearbyGyms

    @BeforeEach
    fun setUp() {
        findNearbyGyms = FindNearbyGyms(gymsRepository, locationManager)

        every { locationManager.getDistanceBetweenPoints(any(), any()) } returns 1000000.0
    }

    @Test
    fun `should return gyms sorted by distance when repository returns data`() = runTest {
        // Given
        val currentLocation = GeoPoint(50.1, 30.1)
        val gyms = listOf(
            Gym(
                address = "Unknown Address",
                communityCenter = "Community Center A",
                facilityTitle = "Gym A",
                group = "Group A",
                location = "Location A",
                openGym = "9:00 AM",
                openGymEnd = "6:00 PM",
                openGymStart = "9:00 AM",
                passType = "Monthly",
                postalCode = "00000",
                provinceCode = "ON",
                total = 100,
                totalFemales = 50,
                totalMales = 50,
                totalNonResidents = 0,
                totalResidents = 100,
                distance = null,
                geoPoint = GeoPoint(50.0, 30.0),
                isFavorite = false,
                isSkipped = false
            ), Gym(
                address = "Unknown Address",
                communityCenter = "Community Center B",
                facilityTitle = "Gym B",
                group = "Group B",
                location = "Location B",
                openGym = "10:00 AM",
                openGymEnd = "7:00 PM",
                openGymStart = "10:00 AM",
                passType = "Daily",
                postalCode = "11111",
                provinceCode = "BC",
                total = 80,
                totalFemales = 40,
                totalMales = 40,
                totalNonResidents = 5,
                totalResidents = 75,
                distance = null,
                geoPoint = GeoPoint(51.0, 31.0),
                isFavorite = false,
                isSkipped = true
            ), Gym(
                address = "Unknown Address",
                communityCenter = "Community Center C",
                facilityTitle = "Gym C",
                group = "Group C",
                location = "Location C",
                openGym = "8:00 AM",
                openGymEnd = "6:00 PM",
                openGymStart = "8:00 AM",
                passType = "Annual",
                postalCode = "22222",
                provinceCode = "QC",
                total = 120,
                totalFemales = 60,
                totalMales = 50,
                totalNonResidents = 10,
                totalResidents = 110,
                distance = null,
                geoPoint = GeoPoint(52.0, 32.0),
                isFavorite = true,
                isSkipped = false
            )
        )

        every { locationManager.getDistanceBetweenPoints(currentLocation, gyms[0].geoPoint!!) } returns 1000.0
        coEvery { gymsRepository.searchNearbyGyms() } returns flowOf(Result.Success(gyms))

        // When
        val result = findNearbyGyms(currentLocation).first()

        // Then
        val expectedGyms = result.getOrDefault(null)
        assertTrue(result.isSucceeded())
        assertEquals(3, expectedGyms?.size)
        assertEquals(1000.0, expectedGyms?.get(0)?.distance)
    }

    @Test
    fun `should return empty list when repository returns empty list`() = runTest {
        // Given
        val currentLocation = GeoPoint(50.0, 30.0)
        coEvery { gymsRepository.searchNearbyGyms() } returns flowOf(Result.Success(emptyList()))

        // When
        val result = findNearbyGyms(currentLocation).first()

        // Then
        assertTrue(result.isSucceeded())
        assertEquals(emptyList<Gym>(), result.getOrDefault(null))
    }

    @Test
    fun `should return error when repository returns failure`() = runTest {
        // Given
        val currentLocation = GeoPoint(50.0, 30.0)
        val error = DataError.Remote.SERVER
        coEvery { gymsRepository.searchNearbyGyms() } returns
                flowOf(Result.Error(error))

        // When
        val result = findNearbyGyms(currentLocation).first()

        // Then
        assertFalse(result.isSucceeded())
        assertEquals(error, result.errorOrNull())
    }

    @Test
    fun `should ignore gyms with null geoPoint`() = runTest {
        // Given
        val currentLocation = GeoPoint(50.0, 30.0)
        val gyms = listOf(
            Gym(
                address = "Unknown Address",
                communityCenter = "Community Center A",
                facilityTitle = "Gym A",
                group = "Group A",
                location = "Location A",
                openGym = "9:00 AM",
                openGymEnd = "6:00 PM",
                openGymStart = "9:00 AM",
                passType = "Monthly",
                postalCode = "00000",
                provinceCode = "ON",
                total = 100,
                totalFemales = 50,
                totalMales = 50,
                totalNonResidents = 0,
                totalResidents = 100,
                distance = null,
                geoPoint = GeoPoint(50.1, 30.1),
                isFavorite = false,
                isSkipped = false
            ), Gym(
                address = "Unknown Address",
                communityCenter = "Community Center B",
                facilityTitle = "Gym B",
                group = "Group B",
                location = "Location B",
                openGym = "10:00 AM",
                openGymEnd = "7:00 PM",
                openGymStart = "10:00 AM",
                passType = "Daily",
                postalCode = "11111",
                provinceCode = "BC",
                total = 80,
                totalFemales = 40,
                totalMales = 40,
                totalNonResidents = 5,
                totalResidents = 75,
                distance = null,
                geoPoint = null,
                isFavorite = false,
                isSkipped = true
            ), Gym(
                address = "Unknown Address",
                communityCenter = "Community Center C",
                facilityTitle = "Gym C",
                group = "Group C",
                location = "Location C",
                openGym = "8:00 AM",
                openGymEnd = "6:00 PM",
                openGymStart = "8:00 AM",
                passType = "Annual",
                postalCode = "22222",
                provinceCode = "QC",
                total = 120,
                totalFemales = 60,
                totalMales = 50,
                totalNonResidents = 10,
                totalResidents = 110,
                distance = null,
                geoPoint = GeoPoint(50.05, 30.05),
                isFavorite = true,
                isSkipped = false
            )
        )

        every { locationManager.getDistanceBetweenPoints(currentLocation, gyms[0].geoPoint!!) } returns 500.0
        coEvery { gymsRepository.searchNearbyGyms() } returns
                flowOf(Result.Success(gyms))

        // When
        val result = findNearbyGyms(currentLocation).first()

        // Then
        val actualGyms = result.getOrDefault(null)
        assertTrue(result.isSucceeded())
        assertEquals(2, actualGyms?.size)
        assertEquals(500.0, actualGyms?.get(0)?.distance)
    }

    @Test
    fun `should return gyms sorted correctly even with extreme distances`() = runTest {
        // Given
        val currentLocation = GeoPoint(0.0, 0.0)
        val gyms = listOf(
            Gym(
                address = "Unknown Address", // Example placeholder, update as needed
                communityCenter = "Community Center A", // Example placeholder, update as needed
                facilityTitle = "Near Gym", // Using name as facilityTitle
                group = "Group A", // Example placeholder, update as needed
                location = "Location A", // Example placeholder, update as needed
                openGym = "9:00 AM", // Example placeholder, update as needed
                openGymEnd = "6:00 PM", // Example placeholder, update as needed
                openGymStart = "9:00 AM", // Example placeholder, update as needed
                passType = "Monthly", // Example placeholder, update as needed
                postalCode = "00000", // Example placeholder, update as needed
                provinceCode = "ON", // Example placeholder, update as needed
                total = 100, // Example placeholder, update as needed
                totalFemales = 50, // Example placeholder, update as needed
                totalMales = 50, // Example placeholder, update as needed
                totalNonResidents = 0, // Example placeholder, update as needed
                totalResidents = 100, // Example placeholder, update as needed
                distance = null, // Example placeholder, update as needed
                geoPoint = GeoPoint(0.001, 0.001), // Using provided geoPoint for the near gym
                isFavorite = false, // Example placeholder, update as needed
                isSkipped = false // Example placeholder, update as needed
            ), Gym(
                address = "Unknown Address",
                communityCenter = "Community Center B",
                facilityTitle = "Far Gym",
                group = "Group B",
                location = "Location B",
                openGym = "10:00 AM",
                openGymEnd = "7:00 PM",
                openGymStart = "10:00 AM",
                passType = "Daily",
                postalCode = "11111",
                provinceCode = "BC",
                total = 80,
                totalFemales = 40,
                totalMales = 40,
                totalNonResidents = 5,
                totalResidents = 75,
                distance = null,
                geoPoint = GeoPoint(80.0, 80.0), // Using provided geoPoint for the far gym
                isFavorite = false,
                isSkipped = true
            )
        )


        every { locationManager.getDistanceBetweenPoints(currentLocation, gyms[0].geoPoint!!) } returns 100.0
        every { locationManager.getDistanceBetweenPoints(currentLocation, gyms[1].geoPoint!!) } returns 200.0
        coEvery { gymsRepository.searchNearbyGyms() } returns flowOf(Result.Success(gyms))

        // When
        val result = findNearbyGyms(currentLocation).first()


        // Then
        val actualGyms = result.getOrDefault(null)
        assertTrue(result.isSucceeded())
        assertEquals(2, actualGyms?.size)
        assertEquals(100.0, actualGyms?.get(0)?.distance)
        assertEquals(200.0, actualGyms?.get(1)?.distance)
    }
}
