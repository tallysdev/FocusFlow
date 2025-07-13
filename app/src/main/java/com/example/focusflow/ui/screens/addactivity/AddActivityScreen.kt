package com.example.focusflow.ui.screens.addactivity

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.focusflow.R
import com.example.focusflow.domain.model.Activity
import com.example.focusflow.domain.model.SubActivity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    viewModel: AddActivityViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Work") }

    // Date picker state
    var selectedDate by remember { mutableStateOf<Calendar?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val subActivities = remember { mutableStateListOf<SubActivityItem>() }
    var newSubActivity by remember { mutableStateOf("") }
    var expandedCategory by remember { mutableStateOf(false) }

    // Drag and drop state
    var draggedItem by remember { mutableStateOf<SubActivityItem?>(null) }
    var dropIndex by remember { mutableIntStateOf(-1) }

    val categories = listOf("Work", "Travel", "Event", "Personal")
    val listState = rememberLazyListState()
    val scrollState = rememberScrollState()
    val subActivitiesScrollState = rememberScrollState()

    // Coletar estado de geração
    val generationState by viewModel.generationState.collectAsState()

    val context = LocalContext.current

    // Date picker dialog
    if (showDatePicker) {
        val datePickerState =
            rememberDatePickerState(
                initialSelectedDateMillis = selectedDate?.timeInMillis,
            )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val newDate =
                            Calendar.getInstance().apply {
                                timeInMillis = it
                            }
                        selectedDate = newDate
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Show dialog with suggested subtasks when is sucess
    if (generationState is GenerationState.Success) {
        val suggestedSubtasks = (generationState as GenerationState.Success).subtasks

        AlertDialog(
            onDismissRequest = { viewModel.resetGenerationState() },
            title = { Text("Suggested subtasks") },
            text = {
                Column {
                    Text("We have generated some subtasks for you. Select the ones you want to add:")
                    Spacer(modifier = Modifier.height(8.dp))

                    suggestedSubtasks.forEach { subtask ->
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            var isSelected by remember { mutableStateOf(false) }

                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { selected ->
                                    isSelected = selected
                                    if (selected) {
                                        subActivities.add(SubActivityItem(subtask))
                                    } else {
                                        subActivities.removeIf { it.text == subtask }
                                    }
                                },
                            )

                            Text(
                                text = subtask,
                                modifier = Modifier.padding(start = 8.dp),
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.resetGenerationState() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8F96CC)),
                ) {
                    Text("Adicionar Selecionadas")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.resetGenerationState() }) {
                    Text("Cancelar")
                }
            },
        )
    }

    // Mostrar indicador de carregamento durante a geração
    if (generationState is GenerationState.Loading) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center,
        ) {
            Card(
                modifier = Modifier.padding(16.dp),
                shape = RoundedCornerShape(8.dp),
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    CircularProgressIndicator(color = Color(0xFF8F96CC))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Gerando subtarefas...")
                }
            }
        }
    }

    LaunchedEffect(generationState) {
        if (generationState is GenerationState.Error) {
            val errorMessage = (generationState as GenerationState.Error).message
            Toast.makeText(context, "Erro: $errorMessage", Toast.LENGTH_LONG).show()
            viewModel.resetGenerationState()
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(listOf(Color(0xFF0A0E20), Color(0xFF1B2448))))
                .padding(16.dp),
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
            Text(
                "Create New Activity",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Conteúdo principal com proporção 50/50
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .weight(1f),
        ) {
            // Activity Details Section - 50% do espaço
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(0.5f), // 50% do espaço
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF21264A)),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState) // Scroll vertical
                                .padding(16.dp),
                    ) {
                        Text(
                            "Activity Details",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Title") },
                            modifier = Modifier.fillMaxWidth(),
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

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
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

                        Spacer(modifier = Modifier.height(8.dp))

                        ExposedDropdownMenuBox(
                            expanded = expandedCategory,
                            onExpandedChange = { expandedCategory = it },
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Category") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory)
                                },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
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

                            ExposedDropdownMenu(
                                expanded = expandedCategory,
                                onDismissRequest = { expandedCategory = false },
                            ) {
                                categories.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            category = option
                                            expandedCategory = false
                                        },
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors =
                                ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color(0xFF2A305A),
                                    contentColor = Color.White,
                                ),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text =
                                        selectedDate?.let {
                                            SimpleDateFormat(
                                                "MM/dd/yyyy",
                                                Locale.getDefault(),
                                            ).format(
                                                it.time,
                                            )
                                        } ?: "Select Due Date",
                                    modifier = Modifier.weight(1f),
                                )
                                Icon(
                                    Icons.Default.DateRange,
                                    contentDescription = "Select Date",
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.generateSubtasks(title, category, description) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = title.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8F96CC)),
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_auto_awesome),
                                contentDescription = "Generate automatically",
                                tint = Color.White,
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate SubActivities")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // SubActivities Section - 50% do espaço
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(0.5f), // 50% do espaço
            ) {
                Card(
                    modifier = Modifier.fillMaxSize(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF21264A)),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                    ) {
                        Text(
                            "SubActivities (Long Press to Drag)",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Add SubActivity Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            OutlinedTextField(
                                value = newSubActivity,
                                onValueChange = { newSubActivity = it },
                                label = { Text("New SubActivity") },
                                modifier = Modifier.weight(1f),
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

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (newSubActivity.isNotBlank()) {
                                        subActivities.add(SubActivityItem(newSubActivity))
                                        newSubActivity = ""
                                    }
                                },
                                modifier =
                                    Modifier
                                        .background(Color(0xFF8F96CC), RoundedCornerShape(4.dp))
                                        .padding(4.dp),
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add SubActivity",
                                    tint = Color.White,
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Conteúdo das subatividades com rolagem vertical
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .verticalScroll(subActivitiesScrollState),
                        ) {
                            subActivities.forEachIndexed { index, item ->
                                val isDragged = draggedItem?.id == item.id

                                Card(
                                    modifier =
                                        Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .shadow(if (isDragged) 8.dp else 0.dp)
                                            .alpha(if (isDragged) 0.6f else 1f)
                                            .pointerInput(item.id) {
                                                detectDragGesturesAfterLongPress(
                                                    onDragStart = {
                                                        draggedItem = item
                                                    },
                                                    onDragEnd = {
                                                        if (dropIndex >= 0 && draggedItem != null) {
                                                            val fromIndex =
                                                                subActivities.indexOfFirst { it.id == draggedItem?.id }
                                                            if (fromIndex >= 0 && fromIndex != dropIndex) {
                                                                val itemToMove =
                                                                    subActivities.removeAt(fromIndex)
                                                                subActivities.add(
                                                                    minOf(
                                                                        dropIndex,
                                                                        subActivities.size,
                                                                    ),
                                                                    itemToMove,
                                                                )
                                                            }
                                                        }
                                                        draggedItem = null
                                                        dropIndex = -1
                                                    },
                                                    onDragCancel = {
                                                        draggedItem = null
                                                        dropIndex = -1
                                                    },
                                                    onDrag = { change, _ ->
                                                        change.consume()
                                                        // Calcular o índice de drop baseado na posição Y do toque
                                                        val itemHeight =
                                                            60.dp.toPx() // altura estimada de um item
                                                        val dragPosition = change.position.y
                                                        val calculatedIndex =
                                                            (dragPosition / itemHeight).toInt()
                                                        dropIndex =
                                                            calculatedIndex.coerceIn(
                                                                0,
                                                                subActivities.size,
                                                            )
                                                    },
                                                )
                                            },
                                    colors =
                                        CardDefaults.cardColors(
                                            containerColor =
                                                if (isDragged) {
                                                    Color(0xFF3A4175)
                                                } else {
                                                    Color(0xFF2A305A)
                                                },
                                        ),
                                ) {
                                    Row(
                                        modifier =
                                            Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        Icon(
                                            painter = painterResource(id = R.drawable.ic_drag),
                                            contentDescription = "Drag",
                                            tint = Color.White,
                                        )

                                        Text(
                                            text = item.text,
                                            color = Color.White,
                                            modifier =
                                                Modifier
                                                    .weight(1f)
                                                    .padding(horizontal = 8.dp),
                                        )

                                        IconButton(
                                            onClick = { subActivities.removeAt(index) },
                                        ) {
                                            Icon(
                                                Icons.Default.Clear,
                                                contentDescription = "Remove",
                                                tint = Color.White,
                                            )
                                        }
                                    }
                                }

                                // Indicador de posição para o arrasto
                                if (draggedItem != null && index == dropIndex) {
                                    Spacer(
                                        modifier =
                                            Modifier
                                                .height(2.dp)
                                                .fillMaxWidth()
                                                .background(Color(0xFF8F96CC)),
                                    )
                                }
                            }

                            // Mostrar a linha indicadora no final da lista
                            if (draggedItem != null && dropIndex >= subActivities.size) {
                                Spacer(
                                    modifier =
                                        Modifier
                                            .height(2.dp)
                                            .fillMaxWidth()
                                            .background(Color(0xFF8F96CC)),
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Save Button
        Button(
            onClick = {
                val activity =
                    createActivity(
                        title,
                        description,
                        category,
                        selectedDate,
                        subActivities,
                    )
                viewModel.saveActivity(activity)
            },
            enabled = title.isNotBlank() && subActivities.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF8F96CC),
                ),
        ) {
            val saveState by viewModel.saveState.collectAsState()

            when (saveState) {
                is SaveState.Saving -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                    )
                }

                else -> {
                    Text("Save Activity")
                }
            }
        }

        val saveState by viewModel.saveState.collectAsState()

        LaunchedEffect(saveState) {
            when (saveState) {
                is SaveState.Success -> {
                    onNavigateBack()
                }

                is SaveState.Error -> {
                    val errorMessage = (saveState as SaveState.Error).message
                    Toast.makeText(
                        context,
                        "Erro ao salvar: $errorMessage",
                        Toast.LENGTH_LONG,
                    ).show()
                }

                else -> {} // Não fazer nada para outros estados
            }
        }
    }
}

// Helper function to create Activity domain object
private fun createActivity(
    title: String,
    description: String,
    category: String,
    selectedDate: Calendar?,
    subActivities: List<SubActivityItem>,
): Activity {
    // Converter SubActivityItem para SubActivity
    val subActivitiesDomain =
        subActivities.mapIndexed { index, item ->
            SubActivity(
                name = item.text,
                status = "pending",
                priority = index,
            )
        }

    return Activity(
        title = title,
        description = description,
        category = category,
        dueDate = selectedDate?.timeInMillis,
        status = "pending",
        subActivities = subActivitiesDomain,
    )
}

data class SubActivityItem(
    val text: String,
    val id: String = UUID.randomUUID().toString(),
)
