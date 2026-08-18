package com.aistudio.detected.stress

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aistudio.detected.stress.ui.screens.AdminAuthScreen
import com.aistudio.detected.stress.ui.screens.AdminDashboardScreen
import com.aistudio.detected.stress.ui.screens.ChatScreen
import com.aistudio.detected.stress.ui.screens.DashboardScreen
import com.aistudio.detected.stress.ui.screens.OnboardingScreen
import com.aistudio.detected.stress.ui.screens.StressAssessmentScreen
import com.aistudio.detected.stress.viewmodel.ChatIntent
import com.aistudio.detected.stress.viewmodel.ChatViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    /*
     * یک instance مشترک: هم صفحهٔ چت و هم پرسشنامه از همین state استفاده میکنند.
     */
    val chatViewModel: ChatViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "intro",
        enterTransition = { fadeIn(animationSpec = tween(700)) },
        exitTransition = { fadeOut(animationSpec = tween(700)) }
    ) {
        composable("intro") {
            OnboardingScreen(
                onNavigateNext = {
                    navController.navigate("main") {
                        popUpTo("intro") { inclusive = true }
                    }
                }
            )
        }

        composable("main") {
            ChatScreen(
                onNavigateStats = {
                    navController.navigate("stats")
                },
                onNavigateAdmin = {
                    navController.navigate("admin_login")
                },
                onNavigateAssessment = {
                    navController.navigate("assessment")
                },
                viewModel = chatViewModel
            )
        }

        composable("assessment") {
            StressAssessmentScreen(
                onCompleted = { result ->
                    chatViewModel.processIntent(
                        ChatIntent.AssessmentCompleted(result)
                    )

                    navController.navigate("main") {
                        popUpTo("assessment") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("stats") {
            DashboardScreen(
                onBack = {
                    navController.popBackStack()
                },
                viewModel = chatViewModel
            )
        }

        composable("admin_login") {
            AdminAuthScreen(
                onLoginSuccess = {
                    navController.navigate("admin_dashboard") {
                        popUpTo("admin_login") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("admin_dashboard") {
            AdminDashboardScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
