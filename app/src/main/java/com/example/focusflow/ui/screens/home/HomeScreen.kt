package com.example.focusflow.ui.screens.home

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.focusflow.R
import com.example.focusflow.domain.model.Activity
import com.example.focusflow.domain.model.SubActivity

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAddActivityClick: () -> Unit,
) {
    // Coletar estados do ViewModel
    val activitiesState by viewModel.activitiesState.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val searchQuery = viewModel.searchQuery

    val filters = listOf("Pending", "Work", "Travel", "Event", "Personal", "Completed")

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(Color(0xFF0A0E20), Color(0xFF1B2448))))
                .padding(16.dp),
    ) {
        // Centralized Title
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(48.dp))
            Text(
                "Activities",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            IconButton(onClick = { onAddActivityClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = "Add Activity",
                    tint = Color.White,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search activities") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            leadingIcon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    tint = Color(0XFF8F96CC),
                    contentDescription = "Search icon",
                )
            },
            colors =
                TextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0XFF21264A),
                    unfocusedPlaceholderColor = Color(0XFF8F96CC),
                    focusedContainerColor = Color(0XFF21264A),
                    focusedPlaceholderColor = Color(0XFF21264A),
                    focusedTextColor = Color(0XFF8F96CC),
                    unfocusedTextColor = Color(0XFF8F96CC),
                ),
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category filters
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
        ) {
            filters.forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { viewModel.updateFilter(filter) },
                    label = {
                        Text(filter, color = Color.White)
                    },
                    border = BorderStroke(1.dp, Color.Transparent),
                    colors =
                        FilterChipDefaults.filterChipColors(
                            containerColor = Color(0XFF21264A),
                            selectedContainerColor = Color(0XFF8F96CC),
                        ),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content based on state
        when (val state = activitiesState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            is UiState.Success -> {
                val activities = state.data
                Log.d("HomeScreen", "Activities loaded: ${activities.size}")

                val filteredActivities =
                    activities.filter { activity ->
                        when (selectedFilter) {
                            "Pending" -> activity.status == "pending"
                            "Completed" -> activity.status == "completed"
                            else -> activity.category == selectedFilter && activity.status == "pending"
                        } && (
                            activity.title.contains(searchQuery, ignoreCase = true) ||
                                activity.description.contains(searchQuery, ignoreCase = true)
                        )
                    }

                if (filteredActivities.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text =
                                when (selectedFilter) {
                                    "Completed" -> "No completed activities found"
                                    "Pending" -> "No pending activities found"
                                    else -> "No activities found in $selectedFilter category"
                                },
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                } else {
                    ActivityList(
                        activities = filteredActivities,
                        viewModel = viewModel,
                    )
                }
            }

            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error loading activities",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = state.message,
                            color = Color(0xFFFF6B6B),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { /* viewModel.retryLoadingActivities() */ },
                            colors =
                                ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF8F96CC),
                                ),
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityList(
    activities: List<Activity>,
    viewModel: HomeViewModel,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = activities,
            key = { it.id },
        ) { activity ->
            ActivityCard(
                activity = activity,
                viewModel = viewModel,
            )
        }
    }
}

@Composable
fun ActivityCard(
    activity: Activity,
    viewModel: HomeViewModel,
) {
    var expanded by remember { mutableStateOf(false) }
    val isCompleted = activity.status == "completed"
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Activity") },
            text = { Text("Are you sure you want to delete '${activity.title}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteActivity(activity.id)
                    showDeleteDialog = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable { expanded = !expanded },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column {
            // Header
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .background(
                            Brush.linearGradient(
                                colors = getCategoryColors(activity.category, isCompleted),
                            ),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = activity.category,
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                    )

                    if (isCompleted) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "✓ Completed",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                        )
                    }
                }

                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier =
                        Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp),
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete Activity",
                        tint = Color.White,
                    )
                }
            }

            // Content
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isCompleted) Color.Gray else Color.Black,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                )

                Text(
                    text = "Due: ${viewModel.formatDate(activity.dueDate)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                )

                if (activity.description.isNotBlank()) {
                    Text(
                        text = activity.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isCompleted) Color.Gray else Color.DarkGray,
                        modifier = Modifier.padding(top = 4.dp),
                        textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                    )
                }

                // Show subactivities when expanded
                if (expanded) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Subtasks",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.DarkGray,
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    if (activity.subActivities.isEmpty()) {
                        Text(
                            text = "No subtasks",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    } else {
                        activity.subActivities.sortedBy { it.priority }.forEach { subActivity ->
                            SubActivityItem(
                                subActivity = subActivity,
                                activityId = activity.id,
                                viewModel = viewModel,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SubActivityItem(
    subActivity: SubActivity,
    activityId: String,
    viewModel: HomeViewModel,
) {
    // Estado local para atualização visual imediata
    var isCompleted by remember { mutableStateOf(subActivity.status == "completed") }

    // Sincronizar o estado local com o estado do modelo
    LaunchedEffect(subActivity.status) {
        isCompleted = subActivity.status == "completed"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable {
                    // Atualizar estado local imediatamente
                    isCompleted = !isCompleted
                    // Atualizar no banco de dados
                    viewModel.toggleSubActivityStatus(
                        activityId,
                        subActivity.id,
                        subActivity.status,
                    )
                },
    ) {
        Checkbox(
            checked = isCompleted, // Usar estado local para resposta imediata
            onCheckedChange = {
                // Atualizar estado local imediatamente
                isCompleted = it
                // Atualizar no banco de dados
                viewModel.toggleSubActivityStatus(
                    activityId,
                    subActivity.id,
                    subActivity.status,
                )
            },
            colors =
                CheckboxDefaults.colors(
                    checkedColor = Color(0xFF1D61E7),
                ),
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = subActivity.name,
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
            color = if (isCompleted) Color.Gray else Color.Black,
        )
    }
}

fun getCategoryColors(
    category: String,
    isCompleted: Boolean = false,
): List<Color> {
    val baseColors =
        when (category) {
            "Work" -> listOf(Color(0xFF4A90E2), Color(0xFF357ABD))
            "Travel" -> listOf(Color(0xFF56CCF2), Color(0xFF2D9CDB))
            "Event" -> listOf(Color(0xFFBB6BD9), Color(0xFF9B51E0))
            "Personal" -> listOf(Color(0xFF6FCF97), Color(0xFF27AE60))
            else -> listOf(Color(0xFF4A90E2), Color(0xFF357ABD))
        }

    return if (isCompleted) {
        baseColors.map { it.copy(alpha = 0.6f) }
    } else {
        baseColors
    }
}

@Preview(showBackground = true, name = "Home Screen Preview", showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        viewModel = TODO(),
        onAddActivityClick = TODO(),
    )
}
