package com.aistudio.detected.stress

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aistudio.detected.stress.ui.screens.OnboardingScreen
import com.aistudio.detected.stress.ui.screens.ChatScreen
import com.aistudio.detected.stress.ui.screens.DashboardScreen
import com.aistudio.detected.stress.ui.screens.StressAssessmentScreen
import com.aistudio.detected.stress.viewmodel.ChatViewModel
import com.aistudio.detected.stress.viewmodel.ChatIntent

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val chatViewModel: ChatViewModel = viewModel()

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
                onNavigateAdmin = { navController.navigate("admin_login") },
                onNavigateAssessment = { navController.navigate("assessment") },
                viewModel = chatViewModel
            )
        }
        composable("stats") {
            DashboardScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("assessment") {
            StressAssessmentScreen(
                onCompleted = { result ->
                    chatViewModel.processIntent(ChatIntent.AssessmentCompleted(result))
                    navController.navigate("main") {
                        popUpTo("assessment") { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }
        composable("assessment_result/{level}/{score}/{maxScore}") { backStackEntry ->
            val level = backStackEntry.arguments?.getString("level") ?: ""
            val score = backStackEntry.arguments?.getString("score") ?: "0"
            val maxScore = backStackEntry.arguments?.getString("maxScore") ?: "16"
            
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.Text("نتیجه ارزیابی", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
                androidx.compose.material3.Text("نمره شما: $score از $maxScore")
                androidx.compose.material3.Text("سطح استرس: $level", modifier = Modifier.padding(vertical = 16.dp))
                androidx.compose.material3.Button(onClick = { navController.popBackStack("main", inclusive = false) }) {
                    androidx.compose.material3.Text("بازگشت به چت")
                }
            }
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
