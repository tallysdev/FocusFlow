package com.example.focusflow.ui.screens.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.focusflow.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onLogout: () -> Unit = {}, // Adicione este parâmetro para navegação após logout
) {
    val userState by viewModel.userState.collectAsState()
    val userStats by viewModel.stats.collectAsState()
    val activities by viewModel.activities.collectAsState()
    val context = LocalContext.current

    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.logout()
                        onLogout()
                        Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT)
                            .show()
                        showLogoutDialog = false
                    },
                ) {
                    Text("Yes", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0F1224))
                .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top Bar com botão de logout
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    showLogoutDialog = true
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Logout",
                    tint = Color.White,
                )
            }

            Text(
                "Profile",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // User Profile Section
        when (userState) {
            is ProfileState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.size(100.dp),
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text("Loading user data...", color = Color.White)
            }

            is ProfileState.Error -> {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = Color.Gray,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    "Error: ${(userState as ProfileState.Error).message}",
                    color = Color.Red,
                )
            }

            is ProfileState.Success -> {
                val user = (userState as ProfileState.Success).user

                // Avatar e Level
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = Color.Gray,
                    ) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }

                    // Level badge
                    Surface(
                        modifier =
                            Modifier
                                .size(36.dp)
                                .border(2.dp, Color(0xFF0F1224), CircleShape),
                        shape = CircleShape,
                        color = Color(0xFF8F96CC),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "${user.level}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    user.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    "@${user.email.substringBefore('@')}",
                    fontSize = 14.sp,
                    color = Color.Gray,
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Points and join date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_auto_awesome),
                        contentDescription = "Points",
                        tint = Color(0xFFFFD700),
                        modifier = Modifier.size(14.dp),
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        "${user.points} points",
                        fontSize = 14.sp,
                        color = Color(0xFFFFD700),
                    )

                    Text(" • ", color = Color.Gray)

                    Text(
                        "Joined ${formatJoinDate(user.createdAt)}",
                        fontSize = 14.sp,
                        color = Color.Gray,
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        "Level ${user.level} • ${
                            calculatePointsToNextLevel(
                                user.level,
                                user.points,
                            )
                        } points to level ${user.level + 1}",
                        color = Color(0xFF8F96CC),
                        fontSize = 12.sp,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Level progress bar
                    LinearProgressIndicator(
                        progress = calculateLevelProgress(user.level, user.points),
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                        color = Color(0xFF8F96CC),
                        trackColor = Color(0xFF21264A),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stats
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                StatItem(
                    number = userStats.tasksCompleted.toString(),
                    label = "Tasks Completed",
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(16.dp))
                StatItem(
                    number = userStats.streaks.toString(),
                    label = "Day Streak",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            StatItem(
                number = userStats.trophies.toString(),
                label = "Trophies Earned",
                modifier = Modifier.fillMaxWidth(),
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Activity
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Recent Activity",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (activities.isEmpty()) {
                Text(
                    "No recent activities",
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 16.dp),
                )
            } else {
                val scrollState = rememberScrollState()

                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .heightIn(max = 200.dp)
                            .verticalScroll(scrollState),
                ) {
                    activities.forEach { activity ->
                        ActivityItem(
                            icon = R.drawable.ic_eye,
                            text = activity.text,
                            time = activity.time,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    number: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier
                .border(width = 1.dp, color = Color(0XFF8f96CC), shape = RoundedCornerShape(10.dp))
                .padding(vertical = 16.dp, horizontal = 20.dp),
    ) {
        Text(text = number, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.LightGray,
            lineHeight = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
fun ActivityItem(
    icon: Int,
    text: String,
    time: String,
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = text, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = time, color = Color(0XFF8F96CC), fontSize = 12.sp)
        }
    }
}

private fun formatJoinDate(timestamp: Long): String {
    val formatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    return formatter.format(Date(timestamp))
}

private fun calculatePointsToNextLevel(
    currentLevel: Int,
    currentPoints: Int,
): Int {
    // Fórmula simples para calcular pontos necessários para o próximo nível
    val pointsForNextLevel = currentLevel * 100
    val pointsNeeded = pointsForNextLevel - currentPoints
    return if (pointsNeeded < 0) 0 else pointsNeeded
}

private fun calculateLevelProgress(
    currentLevel: Int,
    currentPoints: Int,
): Float {
    val pointsForCurrentLevel = (currentLevel - 1) * 100
    val pointsForNextLevel = currentLevel * 100

    val progress =
        (currentPoints - pointsForCurrentLevel).toFloat() / (pointsForNextLevel - pointsForCurrentLevel)
    return progress.coerceIn(0f, 1f)
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun ProfileScreenPreview() {
    // Esta prévia não mostrará dados reais, pois não tem um ViewModel com dados
    ProfileScreen()
}
