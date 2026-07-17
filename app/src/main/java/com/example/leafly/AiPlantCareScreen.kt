package com.example.leafly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leafly.ui.theme.LeaflyGreenCircle
import com.example.leafly.ui.theme.LeaflyGreenDark
import com.example.leafly.ui.theme.LeaflyGreenLight
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ChatMessage(val content: String, val isUser: Boolean)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiPlantCareScreen(
    plantName: String,
    plantSpecies: String,
    onBackClick: () -> Unit
) {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var saveMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        messages.add(ChatMessage(
            content = "Hi! I'm your AI plant care assistant 🌱 I can help you with care tips for your $plantName ($plantSpecies). What would you like to know?",
            isUser = false
        ))
    }

    suspend fun sendToGroq(userMessage: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val client = OkHttpClient()
                val systemPrompt = "You are a helpful plant care expert. The user has a plant called '$plantName' which is a '$plantSpecies'. Give concise, practical advice about caring for this plant. Keep responses under 150 words."
                val messagesArray = JSONArray()
                messagesArray.put(JSONObject().apply { put("role", "system"); put("content", systemPrompt) })
                messages.forEach { msg ->
                    messagesArray.put(JSONObject().apply {
                        put("role", if (msg.isUser) "user" else "assistant")
                        put("content", msg.content)
                    })
                }
                messagesArray.put(JSONObject().apply { put("role", "user"); put("content", userMessage) })

                val body = JSONObject().apply {
                    put("model", "llama-3.3-70b-versatile")
                    put("messages", messagesArray)
                    put("max_tokens", 200)
                }.toString()

                val request = okhttp3.Request.Builder()
                    .url("https://api.groq.com/openai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer $GROQ_API_KEY")
                    .addHeader("Content-Type", "application/json")
                    .post(body.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""
                val json = JSONObject(responseBody)
                json.getJSONArray("choices").getJSONObject(0).getJSONObject("message").getString("content")
            } catch (e: Exception) {
                "Sorry, I couldn't get a response. Please check your API key."
            }
        }
    }

    fun saveTipToFirestore(tip: String) {
        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val tipData = hashMapOf(
            "id" to System.currentTimeMillis().toString(),
            "userId" to userId,
            "plantName" to plantName,
            "tip" to tip,
            "savedAt" to date
        )
        FirebaseFirestore.getInstance()
            .collection("savedTips")
            .add(tipData)
            .addOnSuccessListener { saveMessage = "Tip saved! ✅" }
            .addOnFailureListener { saveMessage = "Failed to save tip" }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("AI Plant Care", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                        Text(plantName, color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LeaflyGreenDark)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            if (saveMessage.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = LeaflyGreenLight)
                ) {
                    Text(saveMessage, color = LeaflyGreenDark, fontSize = 13.sp,
                        modifier = Modifier.padding(12.dp))
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                state = listState,
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    ChatBubble(
                        message = message,
                        onSaveTip = if (!message.isUser) {
                            { saveTipToFirestore(message.content) }
                        } else null
                    )
                }
                if (isLoading) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Box(
                                modifier = Modifier.size(32.dp).clip(CircleShape).background(LeaflyGreenDark),
                                contentAlignment = Alignment.Center
                            ) { Text("🌱", fontSize = 16.sp) }
                            Spacer(modifier = Modifier.width(8.dp))
                            Card(
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 16.dp),
                                colors = CardDefaults.cardColors(containerColor = LeaflyGreenLight)
                            ) {
                                Box(modifier = Modifier.padding(12.dp)) {
                                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = LeaflyGreenDark, strokeWidth = 2.dp)
                                }
                            }
                        }
                    }
                }
            }

            LaunchedEffect(messages.size) {
                if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Ask about $plantName...", color = Color.LightGray) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LeaflyGreenDark,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    ),
                    maxLines = 3
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier.size(52.dp).clip(CircleShape)
                        .background(if (inputText.isBlank() || isLoading) Color.LightGray else LeaflyGreenDark),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = {
                            if (inputText.isBlank() || isLoading) return@IconButton
                            val userMessage = inputText.trim()
                            inputText = ""
                            saveMessage = ""
                            messages.add(ChatMessage(userMessage, isUser = true))
                            isLoading = true
                            coroutineScope.launch {
                                val response = sendToGroq(userMessage)
                                messages.add(ChatMessage(response, isUser = false))
                                isLoading = false
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage, onSaveTip: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(LeaflyGreenDark),
                contentAlignment = Alignment.Center
            ) { Text("🌱", fontSize = 16.sp) }
            Spacer(modifier = Modifier.width(8.dp))
        }
        Column(horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start) {
            Card(
                shape = RoundedCornerShape(
                    topStart = if (message.isUser) 16.dp else 4.dp,
                    topEnd = if (message.isUser) 4.dp else 16.dp,
                    bottomStart = 16.dp, bottomEnd = 16.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (message.isUser) LeaflyGreenDark else LeaflyGreenLight
                ),
                modifier = Modifier.widthIn(max = 280.dp)
            ) {
                Text(
                    text = message.content,
                    modifier = Modifier.padding(12.dp),
                    color = if (message.isUser) Color.White else Color.Black,
                    fontSize = 14.sp, lineHeight = 20.sp
                )
            }
            // Save tip button for AI responses
            if (!message.isUser && onSaveTip != null) {
                TextButton(
                    onClick = onSaveTip,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text("💾 Save tip", fontSize = 12.sp, color = LeaflyGreenDark, fontWeight = FontWeight.Medium)
                }
            }
        }
        if (message.isUser) {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier.size(32.dp).clip(CircleShape).background(LeaflyGreenDark),
                contentAlignment = Alignment.Center
            ) { Text("👤", fontSize = 16.sp) }
        }
    }
}
