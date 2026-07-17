package com.example.leafly

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leafly.ViewModel.PlantViewModel
import com.example.leafly.ViewModel.UserViewModel
import com.example.leafly.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject


@Composable
fun Dashboard(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    plantViewModel: PlantViewModel,
    onPlantsClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
    onAlertsClick: () -> Unit = {},
    onExportClick: () -> Unit = {},
    onSavedTipsClick: () -> Unit = {}
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val plants by plantViewModel.plants.collectAsState()
    val user by userViewModel.users.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            plantViewModel.getPlantsByUser(userId)
            userViewModel.getUserById(userId)
        }
    }

    val totalPlants = plants.size
    val waterToday = plants.count { it.wateringFrequencyDays <= 1 }
    val overdue = plants.count { it.wateringFrequencyDays == 0 }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            DashboardBottomNavigation(
                onPlantsClick = onPlantsClick,
                onAlertsClick = onAlertsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues).background(Color.White)) {
            item {
                DashboardHeader(
                    userName = user?.name ?: "Gardener",
                    totalPlants = totalPlants,
                    waterToday = waterToday,
                    overdue = overdue
                )
            }

            // AI Bot Widget
            item { AiBotWidget() }

            // Quick Actions
            item {
                Text("QUICK ACTIONS", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionCard(emoji = "📥", label = "Export CSV", onClick = onExportClick, modifier = Modifier.weight(1f))
                    QuickActionCard(emoji = "💡", label = "Saved Tips", onClick = onSavedTipsClick, modifier = Modifier.weight(1f))
                    QuickActionCard(emoji = "🔔", label = "Reminders", onClick = onAlertsClick, modifier = Modifier.weight(1f))
                }
            }

            item {
                Text("TODAY'S TASKS", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
            }
            if (plants.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("No plants yet — add one from the Plants tab!", color = Color.Gray, fontSize = 14.sp)
                    }
                }
            } else {
                items(plants.take(3)) { plant ->
                    PlantTaskItem(
                        name = plant.name,
                        status = "Water every ${plant.wateringFrequencyDays} days",
                        tagText = if (plant.wateringFrequencyDays <= 1) "Today" else "Soon",
                        dotColor = if (plant.wateringFrequencyDays <= 1) LeaflyTodayText else LeaflySoonText,
                        tagBgColor = if (plant.wateringFrequencyDays <= 1) LeaflyTodayBrown else LeaflySoonGreen,
                        tagTextColor = if (plant.wateringFrequencyDays <= 1) LeaflyTodayText else LeaflySoonText
                    )
                }
            }

            item {
                Text("MY PLANTS", color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp))
            }
            item {
                if (plants.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        Text("🌱 No plants yet", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        items(plants) { plant ->
                            RealPlantCard(name = plant.name, species = plant.species, imageBase64 = plant.imageUrl)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiBotWidget() {
    var inputText by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = LeaflyGreenDark),
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(36.dp).clip(CircleShape).background(LeaflyGreenCircle),
                    contentAlignment = Alignment.Center
                ) { Text("🌱", fontSize = 18.sp) }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Leafly AI", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text("Ask me anything about your plants!", fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
                }
            }
            if (response.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f))) {
                    Text(response, fontSize = 13.sp, color = Color.White, modifier = Modifier.padding(12.dp), lineHeight = 18.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = inputText, onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask about plant care...", color = Color.White.copy(alpha = 0.5f), fontSize = 13.sp) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White.copy(alpha = 0.5f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 2
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(
                        if (inputText.isBlank() || isLoading) Color.White.copy(alpha = 0.2f) else LeaflyGreenCircle
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    } else {
                        IconButton(onClick = {
                            if (inputText.isBlank() || isLoading) return@IconButton
                            val question = inputText.trim()
                            inputText = ""
                            isLoading = true
                            response = ""
                            coroutineScope.launch {
                                response = askGroq(question)
                                isLoading = false
                            }
                        }) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = LeaflyGreenDark, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

suspend fun askGroq(question: String): String {
    return withContext(Dispatchers.IO) {
        try {
            val client = OkHttpClient()
            val messages = JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", "You are a helpful plant care expert. Give concise, practical advice in under 100 words.")
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", question)
                })
            }
            val body = JSONObject().apply {
                put("model", "llama-3.3-70b-versatile")
                put("messages", messages)
                put("max_tokens", 150)
            }.toString()

            val request = Request.Builder()
                .url("https://api.groq.com/openai/v1/chat/completions")
                .addHeader("Authorization", "Bearer $GROQ_API_KEY")
                .addHeader("Content-Type", "application/json")
                .post(body.toRequestBody("application/json".toMediaType()))
                .build()

            val responseBody = client.newCall(request).execute().body?.string() ?: ""
            val json = JSONObject(responseBody)
            json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
        } catch (e: Exception) {
            "Sorry, I couldn't get a response. Please check your API key."
        }
    }
}

@Composable
fun QuickActionCard(emoji: String, label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LeaflyGreenLight.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(emoji, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = LeaflyGreenDark, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun DashboardHeader(userName: String, totalPlants: Int, waterToday: Int, overdue: Int, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
            .background(LeaflyHeaderGreen).padding(24.dp)
    ) {
        Text("Good morning 🌱", color = LeaflyGreenCircle, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text("Hey, $userName!", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard("$totalPlants", "Plants", Modifier.weight(1f))
            StatCard("$waterToday", "Water\nToday", Modifier.weight(1f))
            StatCard("$overdue", "Overdue", Modifier.weight(1f))
        }
    }
}

@Composable
fun StatCard(count: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.clip(RoundedCornerShape(12.dp)).background(LeaflyStatCardGreen).padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(count, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(label, color = LeaflyGreenCircle, fontSize = 12.sp, textAlign = TextAlign.Center, lineHeight = 14.sp)
    }
}

@Composable
fun PlantTaskItem(name: String, status: String, tagText: String, dotColor: Color, tagBgColor: Color, tagTextColor: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(dotColor))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(status, fontSize = 14.sp, color = Color.Gray)
            }
            Surface(color = tagBgColor, shape = RoundedCornerShape(16.dp)) {
                Text(tagText, color = tagTextColor, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }
        }
        HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
    }
}

@Composable
fun RealPlantCard(name: String, species: String, imageBase64: String = "", modifier: Modifier = Modifier) {
    val imageBitmap = remember(imageBase64) {
        if (imageBase64.isNotEmpty()) {
            try {
                val bytes = Base64.decode(imageBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) { null }
        } else null
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LeaflyLightGreenBg),
        modifier = modifier.width(160.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier.size(80.dp).clip(CircleShape).background(LeaflyGreenCircle),
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
                    Text("🌿", fontSize = 36.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text(species, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun DashboardBottomNavigation(modifier: Modifier = Modifier, onPlantsClick: () -> Unit = {}, onAlertsClick: () -> Unit = {}, onProfileClick: () -> Unit = {}) {
    NavigationBar(containerColor = Color.White, modifier = modifier) {
        NavigationBarItem(
            selected = true, onClick = { },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_home), contentDescription = null) },
            label = { Text("Home") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LeaflyGreenBottom, selectedTextColor = LeaflyGreenBottom,
                unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray, indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false, onClick = onPlantsClick,
            icon = { Icon(painter = painterResource(id = R.drawable.ic_leaf), contentDescription = null) },
            label = { Text("Plants") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = LeaflyGreenBottom, selectedTextColor = LeaflyGreenBottom,
                unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray, indicatorColor = Color.Transparent
            )
        )
        NavigationBarItem(
            selected = false, onClick = onAlertsClick,
            icon = { Icon(painter = painterResource(id = R.drawable.ic_reminders), contentDescription = null) },
            label = { Text("Alerts") },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
        )
        NavigationBarItem(
            selected = false, onClick = onProfileClick,
            icon = { Icon(painter = painterResource(id = R.drawable.ic_settings), contentDescription = null) },
            label = { Text("Profile") },
            colors = NavigationBarItemDefaults.colors(unselectedIconColor = Color.Gray, unselectedTextColor = Color.Gray)
        )
    }
}

data class PlantTask(val name: String, val status: String, val tagText: String, val dotColor: Color, val tagBgColor: Color, val tagTextColor: Color)
data class PlantInfo(val name: String, val status: String, val bgColor: Color, val imageRes: Int)
fun getSampleTasks() = listOf(PlantTask("Monstera", "Overdue by 1 day", "Overdue", LeaflyOverdueText, LeaflyOverdueRed, LeaflyOverdueText))
fun getSamplePlants() = listOf(PlantInfo("Snake Plant", "Water in 7 days", LeaflyLightGreenBg, R.drawable.snakeplant))
