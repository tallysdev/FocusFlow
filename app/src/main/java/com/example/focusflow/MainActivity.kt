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
import androidx.navigation.compose.currentBackStackEntryAsState
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

                Scaffold(
                    bottomBar = {
                        if (shouldShowBottomBar(navController)) {
                            BottomNavigationBar(navController = navController)
                        }
                    },
                    containerColor = Color(0xFF0A0D2E)
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = "login_screen",
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable("signup_screen") {
                            SignUpScreen(navController = navController)
                        }
                        composable("login_screen") {
                            LoginScreen(navController = navController)
                        }
                        composable("home_screen") {
                            HomeScreen()
                        }
                        composable("profile_screen") {
                            ProfileScreen()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun shouldShowBottomBar(navController: androidx.navigation.NavController): Boolean {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    return currentRoute in listOf("home_screen", "profile_screen")
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