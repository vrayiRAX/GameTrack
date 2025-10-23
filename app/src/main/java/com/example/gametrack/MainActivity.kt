package com.example.gametrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.gametrack.screens.*
import com.example.gametrack.ui.theme.GameTrackTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameTrackTheme {
                val navController = rememberNavController()
                val gameViewModel: GameViewModel = viewModel()

                Surface(color = MaterialTheme.colorScheme.background) {
                    NavHost(
                        navController = navController,
                        startDestination = "home"
                    ) {
                        composable("home") {
                            HomeScreen(navController = navController, viewModel = gameViewModel)
                        }
                        composable("add") {
                            AddGameScreen(navController = navController, viewModel = gameViewModel)
                        }
                    }
                }
            }
        }
    }
}
