package com.example.gametrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gametrack.ui.theme.GameTrackTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gametrack.screens.HomeScreen
import com.example.gametrack.screens.AddGameScreen
import com.example.gametrack.screens.LoginScreen
import androidx.compose.foundation.layout.fillMaxSize

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameTrackTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val gameViewModel: GameViewModel = viewModel()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        composable("login") {
                            LoginScreen(navController = navController)
                        }
                        composable("home") {
                            HomeScreen(navController, gameViewModel)
                        }
                        composable("add") {
                            AddGameScreen(navController, gameViewModel)
                        }
                    }
                }
            }
        }
    }
}