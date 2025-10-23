package com.example.gametrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gametrack.GameViewModel
import com.example.gametrack.screens.AddGameScreen
import com.example.gametrack.screens.HomeScreen

@Composable
fun NavGraph(navController: NavHostController, viewModel: GameViewModel) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController, viewModel) }
        composable("add") { AddGameScreen(navController, viewModel) }
    }
}
