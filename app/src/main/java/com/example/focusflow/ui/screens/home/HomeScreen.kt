package com.example.focusflow.ui.screens.home

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.focusflow.R
import com.example.focusflow.ui.screens.home.Task
import com.example.focusflow.ui.screens.home.TaskGroup
import com.example.focusflow.ui.screens.home.TaskGroupCard
import com.example.focusflow.ui.screens.profile.ProfileScreen


@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val groups = viewModel.groups
    var searchQuery by remember { mutableStateOf("") }
    val filters = listOf("Upcoming", "Work", "Travel", "Event")
    var selectedFilter by remember { mutableStateOf(filters.first()) }

    val filteredGroups = groups.filter {
        selectedFilter == "Upcoming" || it.category == selectedFilter
    }.filter {
        it.title.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color(0xFF0A0E20), Color(0xFF1B2448))))
            .padding(16.dp)
    ) {
        // Centralized Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(48.dp))
            Text(
                "Activities",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { /* Handle menu click */ }) {
                Icon(painter = painterResource(id = R.drawable.ic_plus), contentDescription = "Menu", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search activities") },
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    tint = Color(0XFF8F96CC),
                    contentDescription = "Icone busca",
                )
            },
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0XFF21264A),
                unfocusedPlaceholderColor = Color(0XFF8F96CC),
                focusedContainerColor = Color(0XFF21264A),
                focusedPlaceholderColor = Color(0XFF21264A),
                focusedTextColor = Color(0XFF8F96CC)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category filters
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { selectedFilter = filter },
                    label = {
                        Text(filter, color = Color.White)
                    },
                    border = BorderStroke(1.dp, Color.Transparent),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Color(0XFF21264A),
                        selectedContainerColor = Color(0XFF8F96CC)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Filtered task groups
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredGroups) { group ->
                TaskGroupCard(
                    group = group,
                    onToggle = { task -> viewModel.toggle(task) }
                )
            }
        }
    }
}


@Composable
fun TaskGroupCard(group: TaskGroup, onToggle: (Task) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF4A90E2),
                                Color(0xFF357ABD)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {

            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = group.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black
                )
                Text(
                    text = "Due: ${group.dueDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    group.tasks.forEach { task ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { onToggle(task) } // Clique em qualquer lugar da linha
                        ) {
                            Checkbox(
                                checked = task.done,
                                onCheckedChange = { onToggle(task) },
                                colors = CheckboxDefaults.colors(
                                    checkedColor = Color(0xFF1D61E7)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = task.title,
                                style = MaterialTheme.typography.bodyLarge,
                                textDecoration = if (task.done) TextDecoration.LineThrough else null,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
