package com.insearching.urbansports.gyms.domain.usecase

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.gyms.domain.GymsRepository
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.domain.model.Gym
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DislikeGymTest {

    private lateinit var dislikeGym: DislikeGym

    private val gymRepository = mockk<GymsRepository>(relaxed = true)

    @BeforeEach
    fun setUp() {
        dislikeGym = DislikeGym(gymRepository)
    }

    @Test
    fun `should update gym with isSkipped set to true`() = runTest {
        // Given
        val gym = Gym(
            address = "123 Street",
            communityCenter = "Community A",
            facilityTitle = "Gym A",
            group = null,
            location = "City X",
            openGym = "Yes",
            openGymEnd = "22:00",
            openGymStart = "08:00",
            passType = "Monthly",
            postalCode = "12345",
            provinceCode = "XY",
            total = 50,
            totalFemales = 20,
            totalMales = 30,
            totalNonResidents = 5,
            totalResidents = 45,
            geoPoint = GeoPoint(50.0, 30.0),
            isFavorite = false,
            isSkipped = false
        )

        coEvery { gymRepository.updateGym(any()) } returns Result.Success(Unit)

        // When
        val result = dislikeGym.invoke(gym)

        // Then
        assertEquals(Result.Success(Unit), result)
        coVerify { gymRepository.updateGym(gym.copy(isSkipped = true)) }
    }

    @Test
    fun `should return error when update fails`() = runTest {
        // Given
        val gym = Gym(
            address = "123 Street",
            communityCenter = "Community A",
            facilityTitle = "Gym A",
            group = null,
            location = "City X",
            openGym = "Yes",
            openGymEnd = "22:00",
            openGymStart = "08:00",
            passType = "Monthly",
            postalCode = "12345",
            provinceCode = "XY",
            total = 50,
            totalFemales = 20,
            totalMales = 30,
            totalNonResidents = 5,
            totalResidents = 45,
            geoPoint = GeoPoint(50.0, 30.0),
            isFavorite = false,
            isSkipped = false
        )

        val error = DataError.Local.DATA_ERROR
        coEvery { gymRepository.updateGym(any()) } returns Result.Error(error)

        // When
        val result = dislikeGym.invoke(gym)

        // Then
        assertEquals(Result.Error(error), result)
        coVerify { gymRepository.updateGym(gym.copy(isSkipped = true)) }
    }
}
