package com.example.gametrack.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gametrack.GameViewModel
import com.example.gametrack.screens.AddGameScreen
import com.example.gametrack.screens.HomeScreen
import com.example.gametrack.screens.LoginScreen
import com.example.gametrack.screens.ProfileScreen

@Composable
fun NavGraph(navController: NavHostController, viewModel: GameViewModel) {
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(navController = navController, viewModel = viewModel) // ← Agregar viewModel aquí
        }
        composable("home") {
            HomeScreen(navController, viewModel)
        }
        composable("add") {
            AddGameScreen(navController, viewModel)
        }
        composable("profile") {
            ProfileScreen(navController, viewModel)
        }
    }
}