package com.example.aplicacionnba

import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import java.util.Calendar

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "nbaApp"
    ) {
        composable("nbaApp") {
            NBAApp(navController = navController)
        }
        composable("createPlayerScreen") {
            CreatePlayerScreen(
                onPlayerCreated = { /* handle player creation here */ },
                navController = navController
            )
        }
    }
}

