package com.example.leafly

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leafly.ui.theme.LeaflyGreenDark
import com.example.leafly.ui.theme.LeaflyGreenLight
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class SavedTip(
    val id: String = "",
    val plantName: String = "",
    val tip: String = "",
    val savedAt: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedTipsScreen(onBackClick: () -> Unit) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var savedTips by remember { mutableStateOf<List<SavedTip>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load saved tips from Firestore
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            FirebaseFirestore.getInstance()
                .collection("savedTips")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    savedTips = result.documents.map { doc ->
                        SavedTip(
                            id = doc.id,
                            plantName = doc.getString("plantName") ?: "",
                            tip = doc.getString("tip") ?: "",
                            savedAt = doc.getString("savedAt") ?: ""
                        )
                    }.sortedByDescending { it.savedAt }
                    isLoading = false
                }
                .addOnFailureListener { isLoading = false }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Saved Tips", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LeaflyGreenDark)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = LeaflyGreenDark)
            }
        } else if (savedTips.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💡", fontSize = 64.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No saved tips yet", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Go to AI Plant Care and tap", fontSize = 14.sp, color = Color.Gray)
                    Text("\"💾 Save tip\" on any AI response", fontSize = 14.sp, color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(savedTips) { tip ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = LeaflyGreenLight.copy(alpha = 0.3f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("🌱 ${tip.plantName}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                                IconButton(
                                    onClick = {
                                        // Delete from Firestore
                                        FirebaseFirestore.getInstance()
                                            .collection("savedTips")
                                            .document(tip.id)
                                            .delete()
                                        savedTips = savedTips.filter { it.id != tip.id }
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete",
                                        tint = Color.Red.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(tip.tip, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Saved: ${tip.savedAt}", fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
