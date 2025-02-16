package com.insearching.urbansports.gyms.data.repository

import android.database.sqlite.SQLiteException
import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.core.domain.util.errorOrNull
import com.insearching.urbansports.core.domain.util.getOrDefault
import com.insearching.urbansports.core.domain.util.isSucceeded
import com.insearching.urbansports.gyms.data.database.GymDao
import com.insearching.urbansports.gyms.data.mapper.toEntity
import com.insearching.urbansports.gyms.data.networking.RemoteGymDataSource
import com.insearching.urbansports.gyms.data.networking.dto.GymDto
import com.insearching.urbansports.gyms.data.networking.dto.GymResponseDto
import com.insearching.urbansports.gyms.domain.LocationManager
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.domain.model.Gym
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.just
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DefaultGymsRepositoryTest {

    private lateinit var repository: DefaultGymsRepository

    private val remoteGymDataSource: RemoteGymDataSource = mockk()
    private val gymDao: GymDao = mockk(relaxed = true)
    private val locationManager: LocationManager = mockk()

    @BeforeEach
    fun setUp() {
        repository = DefaultGymsRepository(remoteGymDataSource, gymDao, locationManager)
    }

    @Test
    fun `searchNearbyGyms should return cached gyms if available`() = runTest {
        // Given cached gyms exist
        val facilityTitle = "Cached Gym"
        val cachedGyms = listOf(createTestGym(facilityTitle).toEntity())
        coEvery { gymDao.getAllGyms() } returns cachedGyms
        coEvery { locationManager.getLatLngFromAddress(any()) } returns GeoPoint(10.0, 20.0)

        // When
        val result = repository.searchNearbyGyms().first()

        // Then
        assertTrue(result.isSucceeded())
        assertEquals(1, result.getOrDefault(null)?.size)
        assertEquals(facilityTitle, result.getOrDefault(null)?.first()?.facilityTitle)
    }

    @Test
    fun `searchNearbyGyms should fetch gyms from remote when no cache available`() = runTest {
        // Given no cached gyms
        val facilityTitle = "Remote Gym"
        coEvery { gymDao.getAllGyms() } returns emptyList()
//        val remoteGyms = listOf(GymDto(id = "2", name = "Remote Gym", address = "456 Avenue"))
        val remoteGyms = listOf(createTestGym(facilityTitle).toGymDto())
        coEvery { remoteGymDataSource.searchOpenGyms() } returns Result.Success(
            GymResponseDto(remoteGyms)
        )
        coEvery { locationManager.getLatLngFromAddress(any()) } returns GeoPoint(30.0, 40.0)

        // When
        val result = repository.searchNearbyGyms().last()

        // Then
        assertTrue(result.isSucceeded())
        assertEquals(1, result.getOrDefault(null)?.size)
        assertEquals(facilityTitle, result.getOrDefault(null)?.first()?.facilityTitle)
    }

    @Test
    fun `searchNearbyGyms should return error when remote fails`() = runTest {
        // Given remote API fails
        coEvery { gymDao.getAllGyms() } returns emptyList()
        coEvery { remoteGymDataSource.searchOpenGyms() } returns Result.Error(DataError.Remote.SERVER)

        // When
        val result = repository.searchNearbyGyms().last()

        // Then
        assertFalse(result.isSucceeded())
        assertEquals(DataError.Remote.SERVER, result.errorOrNull())
    }

    @Test
    fun `updateGym should return success when update succeeds`() = runTest {
        // Given
        val facilityTitle = "Updated Gym"
        val gym = createTestGym(facilityTitle)
        coEvery { gymDao.updateGym(any()) } just Runs // Simulate successful DB operation

        // When
        val result = repository.updateGym(gym)

        // Then
        assertTrue(result.isSucceeded())
    }

    @Test
    fun `updateGym should return error when SQLiteException occurs`() = runTest {
        // Given
        val facilityTitle = "Failing Gym"
        val gym = createTestGym(facilityTitle)
        coEvery { gymDao.updateGym(any()) } throws SQLiteException("Disk full")

        // When
        val result = repository.updateGym(gym)

        // Then
        assertFalse(result.isSucceeded())
        assertEquals(DataError.Local.DISK_FULL, result.errorOrNull())
    }

    private fun createTestGym(facilityTitle: String): Gym {
        return Gym(
            address = "123 Main St",
            communityCenter = "City Center",
            facilityTitle = facilityTitle,
            group = null,
            location = "Downtown",
            openGym = "Yes",
            openGymEnd = "22:00",
            openGymStart = "08:00",
            passType = "Monthly",
            postalCode = "12345",
            provinceCode = "Province A",
            total = 10,
            totalFemales = 5,
            totalMales = 5,
            totalNonResidents = 2,
            totalResidents = 8,
            geoPoint = GeoPoint(50.0, 30.0),
            isFavorite = false
        )
    }

    private fun Gym.toGymDto() = GymDto(
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
    )
}
