package com.example.gametrack.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
// import androidx.navigation.NavType // <-- Ya no se necesita
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
// import androidx.navigation.navArgument // <-- Ya no se necesita
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

        // --- RUTA "HOME" SIMPLIFICADA ---
        composable(
            route = "home" // <-- 1. Vuelve a ser "home"
        ) {
            // 2. Llama a HomeScreen solo con 2 parámetros
            HomeScreen(navController, gameViewModel)
        }
        // ----------------------------------

        composable("add") {
            AddGameScreen(navController, gameViewModel)
        }
    }
}