package com.example.focusflow.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.focusflow.R

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items =
        listOf(
//        BottomNavItem("signup_screen", Icons.Default.Star, "Sign Up"),
//        BottomNavItem("login_screen", Icons.Default.Lock, "Login"),
            BottomNavItem("home_screen", R.drawable.ic_home, "Home"),
            BottomNavItem("trophies_screen", Icons.Default.Star, "Trophies"),
            BottomNavItem("profile_screen", Icons.Default.Person, "Profile"),
        )

    NavigationBar(
        containerColor = Color(0xFF0A0D2E),
        tonalElevation = 4.dp,
    ) {
        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStackEntry?.destination

        items.forEach { item ->
            val selected = currentDestination?.route == item.route
            NavigationBarItem(
                icon = {
                    when (item.icon) {
                        is ImageVector -> {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.label,
                                tint = if (selected) Color.White else Color(0xFFB0B0D0),
                            )
                        }

                        is Int -> {
                            Icon(
                                painter = painterResource(id = item.icon),
                                contentDescription = item.label,
                                tint = if (selected) Color.White else Color(0xFFB0B0D0),
                            )
                        }

                        else -> {
                            // Optionally, provide a fallback icon or throw an error if the type is unexpected
                            // For now, let's assume it's one of the above or add a placeholder
                            // Icon(Icons.Default.Help, contentDescription = "Unknown icon type")
                        }
                    }
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selected) Color.White else Color(0xFFB0B0D0),
                    )
                },
                selected = selected,
                onClick = {
                    if (currentDestination?.route != item.route) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White, // Explicitly set selected colors
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color(0xFFB0B0D0),
                        unselectedTextColor = Color(0xFFB0B0D0),
                        indicatorColor = Color.Transparent, // Keep indicator transparent if that's the design
                    ),
            )
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: Any,
    val label: String,
)
