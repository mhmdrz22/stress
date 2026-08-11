package com.aistudio.detected.stress

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aistudio.detected.stress.ui.screens.IntroScreen
import com.aistudio.detected.stress.ui.screens.MainScreen
import com.aistudio.detected.stress.ui.screens.StatsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController, 
        startDestination = "intro",
        enterTransition = { fadeIn(animationSpec = tween(700)) },
        exitTransition = { fadeOut(animationSpec = tween(700)) }
    ) {
        composable("intro") {
            IntroScreen(onNavigateNext = {
                navController.navigate("main") {
                    popUpTo("intro") { inclusive = true }
                }
            })
        }
        composable("main") {
            MainScreen(
                onNavigateStats = { navController.navigate("stats") },
                onNavigateAdmin = { navController.navigate("admin_login") }
            )
        }
        composable("stats") {
            StatsScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("admin_login") {
            com.aistudio.detected.stress.ui.screens.AdminLoginScreen(
                onLoginSuccess = { 
                    navController.navigate("admin_dashboard") {
                        popUpTo("admin_login") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("admin_dashboard") {
            com.aistudio.detected.stress.ui.screens.AdminDashboardScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
