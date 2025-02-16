package com.insearching.urbansports.gyms.domain.usecase

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result.Error
import com.insearching.urbansports.core.domain.util.Result.Success
import com.insearching.urbansports.gyms.domain.GymsRepository
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.domain.model.Gym
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.random.Random

class AddGymToFavoritesTest {

    private lateinit var gymRepository: GymsRepository
    private lateinit var addGymToFavorites: AddGymToFavorites

    @BeforeEach
    fun setUp() {
        gymRepository = mockk()
        addGymToFavorites = AddGymToFavorites(gymRepository)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `should return true when gym is updated and hasMatch returns true`() = runTest {
        // Given
        val gym = createTestGym()
        coEvery { gymRepository.updateGym(any()) } returns Success(
            Unit
        )

        mockkStatic("kotlin.random.RandomKt")
        every { (1..100).random() } returns 5 // Within 10% chance

        // When
        val result = addGymToFavorites.invoke(gym)

        // Then
        assertEquals(Success(true), result)
        coVerify { gymRepository.updateGym(gym.copy(isFavorite = true)) }
    }

    @Test
    fun `should return false when gym is updated but hasMatch returns false`() = runTest {
        // Given
        val gym = createTestGym()
        coEvery { gymRepository.updateGym(any()) } returns Success(Unit)

        mockkObject(Random) // Ensure we mock the random object
        every { Random.nextInt(1, 101) } returns 50 // Outside 10% range

        // When
        val result = addGymToFavorites.invoke(gym)

        // Then
        assertEquals(Success(false), result)
        coVerify { gymRepository.updateGym(gym.copy(isFavorite = true)) }
    }

    @Test
    fun `should return failure when updating gym fails`() = runTest {
        // Given
        val gym = createTestGym()
        coEvery { gymRepository.updateGym(any()) } returns Error(DataError.Local.DISK_FULL)

        // When
        val result = addGymToFavorites.invoke(gym)

        // Then
        assertEquals(Error(DataError.Local.DISK_FULL), result)
        coVerify { gymRepository.updateGym(gym.copy(isFavorite = true)) }
    }

    private fun createTestGym(): Gym {
        return Gym(
            address = "123 Main St",
            communityCenter = "City Center",
            facilityTitle = "Gym Facility",
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
}
