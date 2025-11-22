package com.example.gametrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gametrack.ui.theme.GameTrackTheme
import com.example.gametrack.navigation.NavGraph
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GameTrackTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val gameViewModel: GameViewModel = viewModel()

                    NavGraph(navController = navController, viewModel = gameViewModel)
                }
            }
        }
    }
}