package com.aistudio.detected.stress.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.detected.stress.ui.theme.ArameshTheme
import java.security.MessageDigest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminLoginScreen(onLoginSuccess: () -> Unit, onBack: () -> Unit) {
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = ArameshTheme.colors.background,
        topBar = {
            TopAppBar(
                title = { Text("ورود به پنل مدیریت", style = ArameshTheme.typography.title, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "بازگشت")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("رمز عبور مدیر را وارد کنید", style = ArameshTheme.typography.title, color = ArameshTheme.colors.primaryText)
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it; error = false },
                isError = error,
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (error) {
                Text("رمز عبور اشتباه است", color = Color(0xFFEF4444), style = ArameshTheme.typography.label)
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val digest = MessageDigest.getInstance("SHA-256")
                    val hash = digest.digest(password.toByteArray()).joinToString("") { "%02x".format(it) }
                    // Hash for "admin123" is 8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918
                    if (hash == "8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918") {
                        onLoginSuccess()
                    } else {
                        error = true
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ArameshTheme.colors.accentWood)
            ) {
                Text("ورود", color = Color.White)
            }
        }
    }
}
