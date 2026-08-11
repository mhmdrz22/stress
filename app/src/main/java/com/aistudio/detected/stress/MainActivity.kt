package com.aistudio.detected.stress

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.aistudio.detected.stress.ui.theme.ArameshTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArameshTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ArameshTheme.colors.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}
