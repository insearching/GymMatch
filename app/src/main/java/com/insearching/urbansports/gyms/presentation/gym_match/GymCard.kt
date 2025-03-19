package com.insearching.urbansports.gyms.presentation.gym_match

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.Man
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Woman2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.insearching.urbansports.R
import com.insearching.urbansports.core.util.DateUtils.formatTime
import com.insearching.urbansports.gyms.domain.model.Gym
import com.insearching.urbansports.ui.theme.UrbanSportsTheme
import java.util.Locale

@Composable
fun GymCard(
    modifier: Modifier = Modifier,
    gym: Gym,
    onAction: (MatchingScreenAction) -> Unit
) {
    val contentColor = MaterialTheme.colorScheme.onSurface
    val backgrounds = arrayOf(
        R.drawable.pexels_1552248,
        R.drawable.pexels_2652236,
        R.drawable.pexels_3112004,
        R.drawable.pexels_4753987
    )
    val randomBackground = remember { backgrounds.random() }

    Card(
        modifier = modifier.fillMaxSize(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Box {
            Image(
                bitmap = ImageBitmap.imageResource(id = randomBackground),
                contentDescription = "background",
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.tint(
                    color = Color.Black.copy(alpha = 0.7f),  // Adjust alpha for the darkness level
                    blendMode = BlendMode.Multiply
                ),
                modifier = Modifier
                    .blur(
                        radiusX = 10.dp,
                        radiusY = 10.dp,
                        edgeTreatment = BlurredEdgeTreatment(MaterialTheme.shapes.medium)
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(horizontal = 24.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(Color.White.copy(alpha = 0.3f))
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.Start
            ) {
                Column {
                    Text(
                        text = gym.location,
                        style = MaterialTheme.typography.headlineLarge,
                        color = contentColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = gym.facilityTitle,
                        style = MaterialTheme.typography.headlineMedium,
                        color = contentColor,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = gym.getFormattedAddress(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.Start),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .align(Alignment.CenterHorizontally)
                ) {
                    Column {
                        Text(
                            text = gym.openGym,
                            color = contentColor,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                        )
                        Text(
                            text = gym.communityCenter,
                            color = contentColor,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                    VerticalDivider(
                        thickness = 2.dp,
                        modifier = Modifier
                            .fillMaxHeight()
                    )
                    Column {
                        Text(
                            text = gym.passType,
                            color = contentColor,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = gym.group ?: "",
                            color = contentColor,
                            style = MaterialTheme.typography.labelMedium,
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Absolute.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.gym_hours),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = contentColor,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "${gym.openGymStart.formatTime()} - ${gym.openGymEnd.formatTime()}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = contentColor,
                            textAlign = TextAlign.Center
                        )
                    }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.distance),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = contentColor,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            "%.2f".format(Locale.getDefault(), gym.distance?.div(1000f) ?: 0f),
                            style = MaterialTheme.typography.bodyLarge,
                            color = contentColor,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium,
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PersonsView(
                            icon = Icons.Filled.Man,
                            number = gym.totalMales,
                            contentColor = contentColor
                        )
                        PersonsView(
                            icon = Icons.Filled.Woman2,
                            number = gym.totalFemales,
                            contentColor = contentColor
                        )
                        PersonsView(
                            icon = Icons.Filled.EmojiPeople,
                            number = gym.totalResidents,
                            contentColor = contentColor
                        )
                        PersonsView(
                            icon = Icons.Filled.PersonOutline,
                            number = gym.totalNonResidents,
                            contentColor = contentColor
                        )
                        PersonsView(
                            icon = Icons.Filled.People,
                            number = gym.total,
                            contentColor = contentColor
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(32.dp, Alignment.CenterHorizontally)
            ) {
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        onAction(MatchingScreenAction.OnGymDisliked(gym))
                    }
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(R.string.skip),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    onClick = {
                        onAction(MatchingScreenAction.OnGymLiked(gym))
                    }
                ) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = stringResource(R.string.like),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun PersonsView(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    number: Int?,
    contentColor: Color
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "icon",
            modifier = Modifier.size(25.dp),
            tint = contentColor,
        )
        Text(
            text = (number ?: 0).toString(),
            style = MaterialTheme.typography.bodySmall,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun GymCardPreviewDarkTheme() {
    UrbanSportsTheme(darkTheme = true) {
        GymCard(
            gym = Gym(
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
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
fun GymCardPreviewLightTheme() {
    UrbanSportsTheme(darkTheme = false) {
        GymCard(
            gym = Gym(
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
            ),
            onAction = {}
        )
    }
}