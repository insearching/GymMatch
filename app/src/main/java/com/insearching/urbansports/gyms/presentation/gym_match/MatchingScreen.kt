package com.insearching.urbansports.gyms.presentation.gym_match

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.insearching.urbansports.R
import com.insearching.urbansports.core.presentation.util.UiText
import com.insearching.urbansports.core.util.ObserveAsEvents
import com.insearching.urbansports.gyms.domain.model.Gym
import com.insearching.urbansports.gyms.presentation.gym_match.animation.GymMatchAnimation
import com.spartapps.swipeablecards.state.rememberSwipeableCardsState
import com.spartapps.swipeablecards.ui.SwipeableCardDirection
import com.spartapps.swipeablecards.ui.SwipeableCards
import com.spartapps.swipeablecards.ui.SwipeableCardsProperties
import org.koin.androidx.compose.koinViewModel

@Composable
fun MatchingScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: MatchingScreenViewModel = koinViewModel(),
    isPermissionGranted: Boolean
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var animationVisible by remember { mutableStateOf(false) }

    LaunchedEffect(isPermissionGranted) {
        if (isPermissionGranted) {
            viewModel.onAction(MatchingScreenAction.OnRefreshGyms)
        }
    }

    ObserveAsEvents(viewModel.matchChannel) {
        animationVisible = true
    }

    MatchingScreen(
        modifier = modifier,
        state = state,
        animationVisible = animationVisible,
        isPermissionGranted = isPermissionGranted,
        onAction = viewModel::onAction,
        onAnimationEnd = {
            animationVisible = false
        }
    )
}

@Composable
fun MatchingScreen(
    modifier: Modifier = Modifier,
    state: MatchingScreenState,
    animationVisible: Boolean,
    isPermissionGranted: Boolean,
    onAction: (MatchingScreenAction) -> Unit = {},
    onAnimationEnd: () -> Unit = {}
) {
    if (!isPermissionGranted) {
        return
    }
    when (state) {
        is MatchingScreenState.Loading -> {
            MatchingScreenLoading(
                modifier = modifier,
            )
        }

        is MatchingScreenState.Error -> {
            MatchingScreenError(
                modifier = modifier,
                state = state,
                onAction = onAction
            )
        }

        is MatchingScreenState.Success -> {
            MatchingScreenContent(
                modifier = modifier,
                animationVisible = animationVisible,
                state = state,
                onAction = onAction,
                onAnimationEnd = onAnimationEnd
            )
        }
    }
}

@Composable
fun MatchingScreenLoading(
    modifier: Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircularProgressIndicator()
            Text(
                stringResource(R.string.loading),
                style = MaterialTheme.typography.displayMedium,
            )
        }
    }
}

@Composable
fun MatchingScreenContent(
    modifier: Modifier,
    animationVisible: Boolean,
    state: MatchingScreenState.Success,
    onAction: (MatchingScreenAction) -> Unit,
    onAnimationEnd: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        val gyms = remember { state.data }
        val swipeableCardsState = rememberSwipeableCardsState(
            initialCardIndex = 0,
            itemCount = { gyms.size }
        )

        SwipeableCards(
            items = gyms,
            state = swipeableCardsState,
            properties = SwipeableCardsProperties(
                visibleCardsInStack = 2,
                padding = 5.dp,                        // Stack padding
                swipeThreshold = 50.dp,                 // Swipe distance threshold
                lockBelowCardDragging = true,           // Lock cards below top card
                enableRotation = true,                  // Enable rotation animation
                stackedCardsOffset = 30.dp,             // Offset between cards
                draggingAcceleration = 1.5f             // Drag sensitivity
            ),
            onSwipe = { gym, direction ->
                when (direction) {
                    SwipeableCardDirection.Right -> {
                        onAction(MatchingScreenAction.OnGymLiked(gym))
                    }

                    SwipeableCardDirection.Left -> {
                        onAction(MatchingScreenAction.OnGymDisliked(gym))
                    }
                }

                if (swipeableCardsState.currentCardIndex == gyms.lastIndex) {
                    while (swipeableCardsState.currentCardIndex > 0) {
                        swipeableCardsState.goBack()
                    }
                }
            }
        ) { gym, offset ->
            GymCard(
                modifier = Modifier.fillMaxSize(),
                gym = gym,
                onAction = {
                    when (it) {
                        is MatchingScreenAction.OnGymLiked,
                        is MatchingScreenAction.OnGymDisliked -> {
                            swipeableCardsState.moveNext()

                            if (swipeableCardsState.currentCardIndex == gyms.lastIndex) {
                                while (swipeableCardsState.currentCardIndex > 0) {
                                    swipeableCardsState.goBack()
                                }
                            }
                        }

                        else -> {
                            // do noting
                        }
                    }
                    onAction(it)
                }
            )
        }

        GymMatchAnimation(
            isVisible = animationVisible,
            onAnimationEnd = onAnimationEnd
        )
    }
}

@Composable
fun MatchingScreenError(
    modifier: Modifier,
    state: MatchingScreenState.Error,
    onAction: (MatchingScreenAction) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = modifier,
            text = state.message.asString(),
            style = MaterialTheme.typography.displayMedium,
        )
    }
}

@Preview
@Composable
fun MatchingScreenSuccessPreview() {
    val state = MatchingScreenState.Success(
        data = listOf(
            Gym(
                openGymStart = "2016-11-02T17:00:00+00:00",
                openGymEnd = "2016-11-02T19:00:00+00:00",
                totalFemales = 2,
                totalMales = 1,
                totalNonResidents = 1,
                totalResidents = 2,
                total = 3,
                facilityTitle = "Cary Arts Center",
                location = "Principals Hall",
                address = "101 Dry AVE",
                provinceCode = "NC",
                postalCode = "27511",
                passType = "Open Studio Programs",
                communityCenter = "CAC",
                openGym = "Open Studio",
                group = null,
                distance = null
            )
        )
    )
    MatchingScreen(
        state = state,
        animationVisible = false,
        isPermissionGranted = true
    )
}

@Preview
@Composable
fun MatchingScreenLoadingPreview() {
    val state = MatchingScreenState.Loading
    MatchingScreen(
        state = state,
        animationVisible = false,
        isPermissionGranted = true
    )
}

@Preview
@Composable
fun MatchingScreenErrorPreview() {
    val state = MatchingScreenState.Error(UiText.DynamicString("No gyms found"))
    MatchingScreen(
        state = state,
        animationVisible = false,
        isPermissionGranted = true
    )
}