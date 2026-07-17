package com.example.leafly

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.example.leafly.ViewModel.PlantViewModel
import com.example.leafly.ui.theme.LeaflyGreenDark
import com.example.leafly.ui.theme.LeaflyGreenLight
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExportDataScreen(
    plantViewModel: PlantViewModel,
    onBackClick: () -> Unit
) {
    val plants by plantViewModel.plants.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current
    var exportMessage by remember { mutableStateOf("") }
    var isExporting by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) plantViewModel.getPlantsByUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Export Data", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LeaflyGreenDark)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = LeaflyGreenLight.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("📊 Export Summary", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Total Plants: ${plants.size}", fontSize = 14.sp, color = Color.Gray)
                }
            }

            if (plants.isNotEmpty()) {
                Text("Your Plants:", fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(plants) { plant ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = LeaflyGreenLight.copy(alpha = 0.2f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(plant.name, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                                Text(plant.species, fontSize = 13.sp, color = Color.Gray)
                                Text("💧 Every ${plant.wateringFrequencyDays} days", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            } else {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text("🌱 No plants to export yet", color = Color.Gray)
                }
            }

            if (exportMessage.isNotEmpty()) {
                Text(
                    text = exportMessage,
                    color = if (exportMessage.startsWith("Error")) Color.Red else LeaflyGreenDark,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    isExporting = true
                    exportMessage = ""
                    try {
                        val csvContent = buildString {
                            appendLine("Name,Species,Watering Frequency (days),Sunlight,Notes")
                            plants.forEach { plant ->
                                appendLine("\"${plant.name}\",\"${plant.species}\",${plant.wateringFrequencyDays},\"${plant.sunlight}\",\"${plant.notes}\"")
                            }
                        }
                        val file = File(context.cacheDir, "leafly_plants.csv")
                        FileWriter(file).use { it.write(csvContent) }
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            putExtra(Intent.EXTRA_SUBJECT, "Leafly Plants Export")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share CSV"))
                        exportMessage = "✅ Export ready!"
                    } catch (e: Exception) {
                        exportMessage = "Error: ${e.message}"
                    }
                    isExporting = false
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LeaflyGreenDark),
                enabled = plants.isNotEmpty() && !isExporting
            ) {
                if (isExporting) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("📥 Export as CSV", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
