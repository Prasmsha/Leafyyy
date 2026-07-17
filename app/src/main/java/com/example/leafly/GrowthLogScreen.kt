package com.example.leafly

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.leafly.ViewModel.GrowthLogViewModel
import com.example.leafly.model.GrowthLogModel
import com.example.leafly.ui.theme.LeaflyGreenDark
import com.example.leafly.ui.theme.LeaflyGreenLight
import com.example.leafly.ui.theme.LeaflyTheme
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthLogScreen(
    plantId: String,
    plantName: String,
    growthLogViewModel: GrowthLogViewModel,
    onBackClick: () -> Unit
) {
    val logs by growthLogViewModel.logs.collectAsState()
    val loading by growthLogViewModel.loading.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(plantId) {
        growthLogViewModel.getLogsByPlant(plantId)
    }

    GrowthLogScreenContent(
        plantName = plantName,
        logs = logs,
        loading = loading,
        onBackClick = onBackClick,
        onAddClick = { showAddDialog = true },
        onDeleteLog = { logId -> growthLogViewModel.deleteLog(logId, plantId) }
    )

    if (showAddDialog) {
        AddGrowthLogDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { note ->
                val date = SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Date())
                val log = GrowthLogModel(
                    plantId = plantId,
                    userId = userId,
                    note = note,
                    date = date
                )
                growthLogViewModel.addLog(log) { success, _ ->
                    if (success) {
                        growthLogViewModel.getLogsByPlant(plantId)
                        showAddDialog = false
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrowthLogScreenContent(
    plantName: String,
    logs: List<GrowthLogModel>,
    loading: Boolean,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onDeleteLog: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "$plantName — Growth Logs",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LeaflyGreenDark
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = LeaflyGreenDark,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Log",
                    tint = Color.White
                )
            }
        }
    ) { paddingValues ->
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = LeaflyGreenDark)
            }
        } else if (logs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📓", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No growth logs yet",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = LeaflyGreenDark
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap + to add your first log",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(logs) { log ->
                    GrowthLogItem(
                        log = log,
                        onDelete = {
                            onDeleteLog(log.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun GrowthLogItem(
    log: GrowthLogModel,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = LeaflyGreenLight.copy(alpha = 0.3f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.date,
                    fontSize = 12.sp,
                    color = LeaflyGreenDark.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = log.note,
                    fontSize = 15.sp,
                    color = Color.Black,
                    lineHeight = 22.sp
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AddGrowthLogDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var note by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Add Growth Log",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LeaflyGreenDark
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = {
                        Text(
                            "Note",
                            color = LeaflyGreenDark.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LeaflyGreenDark,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    TextButton(
                        onClick = {
                            if (note.isNotBlank()) onAdd(note)
                        }
                    ) {
                        Text(
                            "Add",
                            color = LeaflyGreenDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GrowthLogScreenPreview() {
    LeaflyTheme {
        GrowthLogScreenContent(
            plantName = "Monstera",
            logs = listOf(
                GrowthLogModel(id = "1", date = "2024-05-20", note = "First leaf appeared!"),
                GrowthLogModel(id = "2", date = "2024-06-01", note = "Grew another 2 inches.")
            ),
            loading = false,
            onBackClick = {},
            onAddClick = {},
            onDeleteLog = {}
        )
    }
}

