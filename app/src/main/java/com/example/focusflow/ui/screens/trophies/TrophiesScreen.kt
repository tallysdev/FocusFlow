package com.example.focusflow.ui.screens.trophies

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TrophiesScreen(viewModel: TrophiesViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val trophies = viewModel.trophies

    LaunchedEffect(key1 = Unit) {
        viewModel.trophyAchievedEvent.collect { event ->
            // Mostrar uma notificação ao usuário
            Toast.makeText(
                context,
                "🏆 Trophy Unlocked: ${event.trophy.name}",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        // Verificar conquistas quando a tela de troféus é aberta
        viewModel.checkForNewAchievements()
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Color(0xFF0A0D2E))
                .padding(16.dp),
    ) {
        Text(
            text = "All Trophies",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            items(trophies) { trophy ->
                TrophyItem(trophy)
            }
        }
    }
}

@Composable
fun TrophyItem(trophy: Trophy) {
    val isUnlocked = trophy.dateAchieved != null

    Column(
        modifier =
            Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isUnlocked) Color(0xFF1B1F3B) else Color(0xFF10142A))
                .padding(8.dp),
    ) {
        Box {
            Image(
                painter = painterResource(id = trophy.imageUrl),
                contentDescription = trophy.name,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .alpha(if (isUnlocked) 1f else 0.5f),
            )

            if (!isUnlocked) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x80000000)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = trophy.name,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = trophy.description,
            color = Color.Gray,
            fontSize = 12.sp,
        )

        if (isUnlocked) {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Achieved: ${trophy.dateAchieved}",
                color = Color(0xFF8F96CC),
                fontSize = 10.sp,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun TrophieScreenPreview() {
    TrophiesScreen()
}
