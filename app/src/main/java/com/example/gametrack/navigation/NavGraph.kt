package com.example.gametrack.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gametrack.GameViewModel
import com.example.gametrack.screens.AddGameScreen
import com.example.gametrack.screens.HomeScreen
import com.example.gametrack.screens.LoginScreen
import com.example.gametrack.screens.SignUpScreen

@Composable
fun NavGraph(navController: NavHostController) {

    val gameViewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(navController = navController, viewModel = gameViewModel)
        }

        composable("signup") {
            SignUpScreen(navController = navController, viewModel = gameViewModel)
        }

        composable(
            route = "home"
        ) {
            HomeScreen(navController, gameViewModel)
        }

        composable("add") {
            AddGameScreen(navController, gameViewModel)
        }
    }
}