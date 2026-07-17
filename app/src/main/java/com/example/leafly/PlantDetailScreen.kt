package com.example.leafly

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.leafly.ViewModel.CareTaskViewModel
import com.example.leafly.ViewModel.PlantViewModel
import com.example.leafly.model.CareTaskModel
import com.example.leafly.ui.theme.LeaflyGreenCircle
import com.example.leafly.ui.theme.LeaflyGreenDark
import com.example.leafly.ui.theme.LeaflyGreenLight
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    plantId: String,
    plantViewModel: PlantViewModel,
    careTaskViewModel: CareTaskViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onGrowthLogClick: () -> Unit,
    onAiClick: () -> Unit
) {
    val selectedPlant by plantViewModel.selectedPlant.collectAsState()
    val tasks by careTaskViewModel.tasks.collectAsState()
    val loading by careTaskViewModel.loading.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(plantId) {
        plantViewModel.getPlantById(plantId)
        careTaskViewModel.getTasksByPlant(plantId)
    }

    // Decode plant image
    val imageBitmap = remember(selectedPlant?.imageUrl) {
        val imageUrl = selectedPlant?.imageUrl ?: ""
        if (imageUrl.isNotEmpty()) {
            try {
                val bytes = Base64.decode(imageUrl, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) { null }
        } else null
    }

    // Delete confirmation dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Delete Plant", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete ${selectedPlant?.name}? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        plantViewModel.deletePlant(plantId) { success, _ ->
                            if (success) onBackClick()
                        }
                        showDeleteConfirmDialog = false
                    }
                ) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Cancel", color = LeaflyGreenDark)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedPlant?.name ?: "Plant Detail", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.White)
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Plant", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LeaflyGreenDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTaskDialog = true }, containerColor = LeaflyGreenDark, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = "Add Task", tint = Color.White)
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                selectedPlant?.let { plant ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LeaflyGreenLight.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Plant image
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(LeaflyGreenCircle),
                                contentAlignment = Alignment.Center
                            ) {
                                if (imageBitmap != null) {
                                    Image(
                                        bitmap = imageBitmap.asImageBitmap(),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Text("🌿", fontSize = 64.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(plant.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                            Text(plant.species, fontSize = 15.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("💧 Water every ${plant.wateringFrequencyDays} days", fontSize = 14.sp, color = LeaflyGreenDark)
                            if (plant.sunlight.isNotEmpty()) Text("☀️ ${plant.sunlight}", fontSize = 14.sp, color = LeaflyGreenDark)
                            if (plant.notes.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("📝 ${plant.notes}", fontSize = 14.sp, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = onGrowthLogClick,
                                colors = ButtonDefaults.buttonColors(containerColor = LeaflyGreenDark),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("📓 View Growth Logs", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium) }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = onAiClick,
                                colors = ButtonDefaults.buttonColors(containerColor = LeaflyGreenDark.copy(alpha = 0.8f)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) { Text("🤖 AI Plant Care Tips", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium) }
                        }
                    }
                }
            }

            item {
                Text("Care Tasks", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp))
            }

            if (loading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LeaflyGreenDark)
                    }
                }
            } else if (tasks.isEmpty()) {
                item { Text("No tasks yet. Tap + to add one.", color = Color.Gray, fontSize = 14.sp) }
            } else {
                items(tasks) { task ->
                    CareTaskItem(
                        task = task,
                        onCheckedChange = { done -> careTaskViewModel.markTaskDone(task.id, done, plantId) },
                        onDelete = { careTaskViewModel.deleteTask(task.id, plantId) }
                    )
                }
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            plantId = plantId,
            userId = userId,
            onDismiss = { showAddTaskDialog = false },
            onAdd = { task ->
                careTaskViewModel.addTask(task) { success, _ ->
                    if (success) {
                        careTaskViewModel.getTasksByPlant(plantId)
                        showAddTaskDialog = false
                    }
                }
            }
        )
    }
}

@Composable
fun CareTaskItem(task: CareTaskModel, onCheckedChange: (Boolean) -> Unit, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = task.done, onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = LeaflyGreenDark)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title, fontSize = 15.sp, fontWeight = FontWeight.Medium,
                    color = if (task.done) Color.Gray else Color.Black,
                    textDecoration = if (task.done) TextDecoration.LineThrough else TextDecoration.None
                )
                if (task.dueDate.isNotEmpty()) Text("Due: ${task.dueDate}", fontSize = 12.sp, color = Color.Gray)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun AddTaskDialog(plantId: String, userId: String, onDismiss: () -> Unit, onAdd: (CareTaskModel) -> Unit) {
    var title by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Add Care Task", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = title, onValueChange = { title = it },
                    label = { Text("Task title", color = LeaflyGreenDark.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LeaflyGreenDark, unfocusedBorderColor = Color.LightGray)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = dueDate, onValueChange = { dueDate = it },
                    label = { Text("Due date (e.g. 2026-07-01)", color = LeaflyGreenDark.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = LeaflyGreenDark, unfocusedBorderColor = Color.LightGray)
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
                    TextButton(onClick = { if (title.isNotBlank()) onAdd(CareTaskModel(plantId = plantId, userId = userId, title = title, dueDate = dueDate)) }) {
                        Text("Add", color = LeaflyGreenDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
