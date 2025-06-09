package com.example.focusflow.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.focusflow.R

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F1224))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { /* Handle menu click */ }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
            }
            Text(
                "Profile",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Avatar
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color.Gray
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Ethan Carter", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Spacer(modifier = Modifier.height(3.dp))
        Text("@ethan_carter", fontSize = 14.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(2.dp))
        Text("Joined 2021", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(24.dp))

        // Stats
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    number = "120",
                    label = "Tasks Completed",
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                StatItem(
                    number = "80",
                    label = "Streaks",
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            StatItem(
                number = "20",
                label = "Trophies",
                modifier = Modifier.fillMaxWidth()
            )
        }


        Spacer(modifier = Modifier.height(24.dp))

        // Activity
        Column(modifier = Modifier.fillMaxWidth()) {
            Text("Activity", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(16.dp))
            ActivityItem(
                icon = R.drawable.ic_eye, // Make sure you have this drawable
                text = "Completed 'Project Alpha' Milestone",
                time = "2 days ago"
            )
            ActivityItem(
                icon = R.drawable.ic_eye, // Make sure you have this drawable
                text = "Achieved 5-Day Streak",
                time = "1 week ago"
            )
            ActivityItem(
                icon = R.drawable.ic_eye, // Make sure you have this drawable
                text = "Joined 'Productivity Masters' Community",
                time = "2 weeks ago"
            )
        }
    }
}

@Composable
fun StatItem(number: String, label: String, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .border(width = 1.dp, color = Color(0XFF8f96CC), shape = RoundedCornerShape(10.dp)) // White border
            .padding(vertical = 16.dp, horizontal = 20.dp)
    ) {
        Text(text = number, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = label, fontSize = 12.sp, color = Color.LightGray, lineHeight = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun ActivityItem(icon: Int, text: String, time: String) {
    Row(modifier = Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = text, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = time, color = Color(0XFF8F96CC), fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun ProfileScreenPreview() {
    // You might want to wrap this in a Theme if you have one defined
    // For example: YourAppTheme { ProfileScreen() }
    ProfileScreen()
}