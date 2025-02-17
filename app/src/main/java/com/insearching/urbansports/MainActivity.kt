package com.insearching.urbansports

import android.Manifest
import android.app.Activity
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.insearching.urbansports.core.navigation.Screen
import com.insearching.urbansports.core.presentation.dialog.CoarseLocationPermissionTextProvider
import com.insearching.urbansports.core.presentation.dialog.PermissionDialog
import com.insearching.urbansports.gyms.presentation.favorite_gyms.FavoritesScreenRoot
import com.insearching.urbansports.gyms.presentation.gym_match.MatchingScreenRoot
import com.insearching.urbansports.gyms.presentation.gym_match.MatchingScreenViewModel
import com.insearching.urbansports.ui.theme.UrbanSportsChallenageTheme
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    private val permissionsToRequest = Manifest.permission.ACCESS_COARSE_LOCATION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            UrbanSportsChallenageTheme {
                val viewModel = koinViewModel<MainViewModel>()
                val dialogQueue = viewModel.visiblePermissionDialogQueue

                val locationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        viewModel.onPermissionResult(
                            permission = permissionsToRequest,
                            isGranted = isGranted
                        )
                    }
                )

                LaunchedEffect(viewModel.visiblePermissionDialogQueue.isEmpty()) {
                    locationPermissionLauncher.launch(permissionsToRequest)
                }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val isPermissionGranted by viewModel.isPermissionsGranted.collectAsState()

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
                                isPermissionGranted = isPermissionGranted
                            )
                        }
                        composable(
                            route = Screen.FavouritesScreen.route
                        ) {
                            FavoritesScreenRoot()
                        }
                    }
                }

                dialogQueue
                    .reversed()
                    .forEach { permission ->
                        PermissionDialog(
                            permissionTextProvider = when (permission) {
                                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                                    CoarseLocationPermissionTextProvider()
                                }

                                else -> return@forEach
                            },
                            isPermanentlyDeclined = !shouldShowRequestPermissionRationale(
                                permission
                            ),
                            onDismiss = viewModel::dismissDialog,
                            onOkClick = {
                                viewModel.dismissDialog()
                                locationPermissionLauncher.launch(permissionsToRequest)
                            },
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
