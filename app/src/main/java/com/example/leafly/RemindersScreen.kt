package com.example.leafly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leafly.ViewModel.PlantViewModel
import com.example.leafly.ui.theme.LeaflyGreenDark
import com.example.leafly.ui.theme.LeaflyGreenLight
import com.example.leafly.ui.theme.LeaflyOverdueRed
import com.example.leafly.ui.theme.LeaflyOverdueText
import com.example.leafly.ui.theme.LeaflySoonGreen
import com.example.leafly.ui.theme.LeaflySoonText
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    plantViewModel: PlantViewModel,
    onBackClick: () -> Unit
) {
    val plants by plantViewModel.plants.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = LocalContext.current
    var testMessage by remember { mutableStateOf("") }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) plantViewModel.getPlantsByUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watering Reminders", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LeaflyGreenDark)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = LeaflyGreenLight.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("💧 Reminder Status", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "Watering reminders are set for all your plants. You'll receive notifications based on each plant's watering schedule.",
                            fontSize = 13.sp, color = Color.Gray, lineHeight = 18.sp
                        )
                    }
                }
            }

            if (testMessage.isNotEmpty()) {
                item {
                    Text(testMessage, color = LeaflyGreenDark, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 4.dp))
                }
            }

            if (plants.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔔", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No plants yet", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                            Text("Add plants to set reminders", fontSize = 13.sp, color = Color.Gray)
                        }
                    }
                }
            } else {
                item {
                    Text("Your Plants & Schedules", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 14.sp)
                }
                items(plants) { plant ->
                    val isOverdue = plant.wateringFrequencyDays == 0
                    val isDueToday = plant.wateringFrequencyDays == 1
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isOverdue -> LeaflyOverdueRed
                                isDueToday -> Color(0xFFFFF3E0)
                                else -> LeaflyGreenLight.copy(alpha = 0.3f)
                            }
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(40.dp).clip(CircleShape).background(
                                    when {
                                        isOverdue -> LeaflyOverdueText.copy(alpha = 0.2f)
                                        isDueToday -> Color(0xFFFFCC80)
                                        else -> LeaflyGreenDark.copy(alpha = 0.1f)
                                    }
                                ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💧", fontSize = 20.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(plant.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                                Text(
                                    text = when {
                                        isOverdue -> "⚠️ Overdue!"
                                        isDueToday -> "🔔 Due today"
                                        else -> "💧 Every ${plant.wateringFrequencyDays} days"
                                    },
                                    fontSize = 13.sp,
                                    color = when {
                                        isOverdue -> LeaflyOverdueText
                                        isDueToday -> Color(0xFFE65100)
                                        else -> Color.Gray
                                    }
                                )
                            }
                            Button(
                                onClick = {
                                    WateringReminderScheduler.scheduleTestReminder(context, plant.name)
                                    testMessage = "✅ Test notification sent for ${plant.name}!"
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LeaflyGreenDark),
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp)
                            ) {
                                Text("Test", fontSize = 12.sp, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}
