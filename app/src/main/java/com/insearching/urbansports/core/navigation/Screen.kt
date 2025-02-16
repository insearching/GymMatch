package com.insearching.urbansports.core.navigation

sealed class Screen(val route: String) {
    data object GymMatchScreen: Screen("gym_match_screen")
    data object FavouritesScreen: Screen("favourites_screen")
}
