package com.insearching.urbansports

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.insearching.urbansports.core.navigation.Screen
import com.insearching.urbansports.core.presentation.dialog.CoarseLocationPermissionTextProvider
import com.insearching.urbansports.core.presentation.dialog.GpsPermissionTextProvider
import com.insearching.urbansports.core.presentation.dialog.PermissionDialog
import com.insearching.urbansports.core.util.LocationUtils.hasLocationPermissions
import com.insearching.urbansports.core.util.LocationUtils.isGpsEnabled
import com.insearching.urbansports.gyms.presentation.favorite_gyms.FavoritesScreenRoot
import com.insearching.urbansports.gyms.presentation.gym_match.MatchingScreenRoot
import com.insearching.urbansports.gyms.presentation.gym_match.MatchingScreenViewModel
import com.insearching.urbansports.ui.theme.UrbanSportsTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val permissionsToRequest = Manifest.permission.ACCESS_COARSE_LOCATION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UrbanSportsTheme {
                var showGpsDialog by remember { mutableStateOf(false) }
                var showPermissionDialog by remember { mutableStateOf(false) }

                var isGpsEnabled by remember { mutableStateOf(isGpsEnabled()) }
                var isPermissionGranted by remember { mutableStateOf(hasLocationPermissions()) }

                val gpsSettingsLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.StartActivityForResult()
                ) {
                    isGpsEnabled = isGpsEnabled()
                }

                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        isPermissionGranted = isGranted
                        if (isGranted) {
                            showPermissionDialog = false
                        }
                    }
                )

                LaunchedEffect(isGpsEnabled) {
                    showGpsDialog = !isGpsEnabled
                }

                LaunchedEffect(showPermissionDialog) {
                    locationPermissionLauncher.launch(permissionsToRequest)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.GymMatchScreen.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(
                            route = Screen.GymMatchScreen.route
                        ) {
                            MatchingScreenRoot(
                                viewModel = koinViewModel<MatchingScreenViewModel>(),
                                isGpsEnabled = isGpsEnabled,
                                isPermissionGranted = isPermissionGranted,
                            )
                        }
                        composable(
                            route = Screen.FavouritesScreen.route
                        ) {
                            FavoritesScreenRoot()
                        }
                    }
                }

                if (showGpsDialog) {
                    PermissionDialog(
                        permissionTextProvider = GpsPermissionTextProvider(),
                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                            permissionsToRequest
                        ),
                        onDismiss = {
                            showPermissionDialog = false
                        },
                        onGoToAppSettingsClick = {
                            gpsSettingsLauncher.launch(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        }
                    )
                }

                if (showPermissionDialog) {
                    PermissionDialog(
                        permissionTextProvider = CoarseLocationPermissionTextProvider(),
                        isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                            permissionsToRequest
                        ),
                        onDismiss = { showPermissionDialog = false },
                        onGoToAppSettingsClick = ::openAppSettings
                    )
                }
            }
        }
    }
}

fun Activity.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}


fun Context.openGpsSettings() {
    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
    startActivity(intent)
}
