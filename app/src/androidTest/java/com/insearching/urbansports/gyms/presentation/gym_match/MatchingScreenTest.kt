package com.insearching.urbansports.gyms.presentation.gym_match

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.insearching.urbansports.core.presentation.util.UiText
import com.insearching.urbansports.gyms.domain.model.GeoPoint
import com.insearching.urbansports.gyms.domain.model.Gym
import io.mockk.mockk
import io.mockk.verify
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MatchingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testLoadingState_isDisplayed() {
        composeTestRule.setContent {
            MatchingScreen(
                state = MatchingScreenState.Loading,
                isPermissionGranted = true,
                animationVisible = false
            )
        }

        composeTestRule.onNodeWithText("Loading…").assertIsDisplayed()
    }

    @Test
    fun testErrorState_isDisplayed() {
        composeTestRule.setContent {
            MatchingScreen(
                state = MatchingScreenState.Error(UiText.DynamicString("Network Error")),
                isPermissionGranted = true,
                animationVisible = false
            )
        }

        composeTestRule.onNodeWithText("Network Error").assertIsDisplayed()
    }

    @Test
    fun testSuccessState_displaysGyms() {
        val gyms = listOf(createTestGym("Gym A"), createTestGym("Gym B"))

        composeTestRule.setContent {
            MatchingScreen(
                state = MatchingScreenState.Success(gyms),
                isPermissionGranted = true,
                animationVisible = false
            )
        }

        composeTestRule.onNodeWithText("Gym A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gym B").assertIsDisplayed()
    }

    // TODO: Fix test when there is a possibility to mock sealed classes
    @Ignore
    @Test
    fun testSwipeRight_callsOnGymLiked() {
        val gyms = listOf(createTestGym("Gym A"))
        val onAction = mockk<(MatchingScreenAction) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            MatchingScreen(
                state = MatchingScreenState.Success(gyms),
                isPermissionGranted = true,
                animationVisible = false,
                onAction = onAction
            )
        }

        composeTestRule.onNodeWithText("Gym A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Like").performClick()

        verify { onAction(MatchingScreenAction.OnGymLiked(gyms.first())) }
    }

    // TODO: Fix test when there is a possibility to mock sealed classes
    @Ignore
    @Test
    fun testSwipeLeft_callsOnGymDisliked() {
        val gyms = listOf(createTestGym("Gym A"))
        val onAction = mockk<(MatchingScreenAction) -> Unit>(relaxed = true)

        composeTestRule.setContent {
            MatchingScreen(
                state = MatchingScreenState.Success(gyms),
                isPermissionGranted = true,
                animationVisible = false,
                onAction = onAction
            )
        }

        composeTestRule.onNodeWithText("Gym A").assertIsDisplayed()
        composeTestRule.onNodeWithText("Skip").performClick()

        verify { onAction(MatchingScreenAction.OnGymDisliked(gyms.first())) }
    }

    private fun createTestGym(facilityTitle: String): Gym {
        return Gym(
            address = "123 Main St",
            communityCenter = "City Center",
            facilityTitle = facilityTitle,
            group = null,
            location = "Downtown",
            openGym = "Yes",
            openGymEnd = "2016-11-02T19:00:00+00:00",
            openGymStart = "2016-11-02T17:00:00+00:00",
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