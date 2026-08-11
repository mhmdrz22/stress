package com.aistudio.detected.stress

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aistudio.detected.stress.ui.screens.OnboardingScreen
import com.aistudio.detected.stress.ui.screens.ChatScreen
import com.aistudio.detected.stress.ui.screens.DashboardScreen

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
            OnboardingScreen(onNavigateNext = {
                navController.navigate("main") {
                    popUpTo("intro") { inclusive = true }
                }
            })
        }
        composable("main") {
            ChatScreen(
                onNavigateStats = { navController.navigate("stats") },
                onNavigateAdmin = { navController.navigate("admin_login") }
            )
        }
        composable("stats") {
            DashboardScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("admin_login") {
            com.aistudio.detected.stress.ui.screens.AdminAuthScreen(
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
