package com.example.focusflow

import com.example.focusflow.ui.screens.home.HomeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.focusflow.navigation.BottomNavigationBar
import com.example.focusflow.ui.screens.login.LoginScreen
import com.example.focusflow.ui.screens.profile.ProfileScreen
import com.example.focusflow.ui.screens.signup.SignUpScreen
import com.example.focusflow.ui.theme.FocusFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FocusFlowTheme {
                val navController = rememberNavController()
//                Scaffold(containerColor = Color(0XFF0A0D2E)) { paddingValues ->
//                    NavHost(
//                        navController = navController,
//                        startDestination = "signup_screen"
//                    ) { // Or "login_screen" if that's your default
//                        composable("signup_screen") {
//                            SignUpScreen(navController = navController) // Pass NavController
//                        }
//                        composable("login_screen") {
//                            LoginScreen(navController = navController) // Pass NavController
//                        }
//                        // Add other destinations like "home_screen"
//                        // composable("home_screen") { HomeScreen(navController = navController) }
//                    }
//                }
                Scaffold(
                    bottomBar = {
                        BottomNavigationBar(navController = navController)
                    },
                    containerColor = Color(0xFF0A0D2E) // Fundo escuro igual ao da imagem
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "home_screen",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("signup_screen") {
                            SignUpScreen(navController = navController)
                        }
                        composable("login_screen") {
                            LoginScreen(navController = navController)
                        }
                        composable("home_screen") { HomeScreen() }
//                        composable("achievements_screen") { AchievementsScreen() }
                        composable("profile_screen") { ProfileScreen() }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    FocusFlowTheme {
        Greeting("Android")
    }
}