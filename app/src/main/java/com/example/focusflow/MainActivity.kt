package com.example.focusflow

import com.example.focusflow.ui.screens.home.HomeScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.focusflow.ui.screens.login.LoginScreen
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
                NavHost(
                    navController = navController,
                    startDestination = "signup_screen"
                ) { // Or "login_screen" if that's your default
                    composable("signup_screen") {
                        SignUpScreen(navController = navController) // Pass NavController
                    }
                    composable("login_screen") {
                        LoginScreen(navController = navController) // Pass NavController
                    }
                    // Add other destinations like "home_screen"
                    // composable("home_screen") { HomeScreen(navController = navController) }
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