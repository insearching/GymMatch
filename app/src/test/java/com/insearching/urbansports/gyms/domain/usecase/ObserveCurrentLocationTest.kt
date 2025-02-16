package com.insearching.urbansports.gyms.domain.usecase

import com.insearching.urbansports.core.domain.util.DataError
import com.insearching.urbansports.core.domain.util.Result
import com.insearching.urbansports.gyms.domain.LocationManager
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.utils.Constants
import io.mockk.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ObserveCurrentLocationTest {

    private lateinit var observeCurrentLocation: ObserveCurrentLocation

    private val locationManager = mockk<LocationManager>(relaxed = true)

    @BeforeEach
    fun setUp() {
        observeCurrentLocation = ObserveCurrentLocation(locationManager)
    }

    @Test
    fun `should emit new location when it changes significantly`() = runTest {
        // Given
        val initialLocation = GeoPoint(50.0, 30.0)
        val newLocation = GeoPoint(50.1, 30.1) // Significant change
        val flow = flowOf(
            Result.Success(initialLocation),
            Result.Success(newLocation)
        )

        coEvery { locationManager.getCurrentLocation() } returns flow
        every { locationManager.getDistanceBetweenPoints(initialLocation, newLocation) } returns (Constants.DISTANCE_THRESHOLD_METERS + 1).toDouble()

        // When
        val results = observeCurrentLocation.invoke().toList()

        // Then
        assertEquals(listOf(Result.Success(initialLocation), Result.Success(newLocation)), results)
    }

    @Test
    fun `should not emit new location if change is insignificant`() = runTest {
        // Given
        val initialLocation = GeoPoint(50.0, 30.0)
        val closeLocation = GeoPoint(50.0001, 30.0001) // Tiny change
        val flow = flowOf(
            Result.Success(initialLocation),
            Result.Success(closeLocation)
        )

        coEvery { locationManager.getCurrentLocation() } returns flow
        every { locationManager.getDistanceBetweenPoints(initialLocation, closeLocation) } returns (Constants.DISTANCE_THRESHOLD_METERS - 1).toDouble()

        // When
        val results = observeCurrentLocation.invoke().toList()

        // Then
        assertEquals(listOf(Result.Success(initialLocation)), results) // Only first emission
    }

    @Test
    fun `should emit error when location retrieval fails`() = runTest {
        // Given
        val error = DataError.Local.GPS_DISABLED
        val flow = flowOf(Result.Error(error))

        coEvery { locationManager.getCurrentLocation() } returns flow

        // When
        val results = observeCurrentLocation.invoke().toList()

        // Then
        assertEquals(listOf(Result.Error(error)), results) // Ensure errors are not filtered out
    }
}
