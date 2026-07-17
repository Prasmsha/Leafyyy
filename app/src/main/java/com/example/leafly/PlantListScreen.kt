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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leafly.ViewModel.PlantViewModel
import com.example.leafly.model.PlantModel
import com.example.leafly.ui.theme.LeaflyGreenCircle
import com.example.leafly.ui.theme.LeaflyGreenDark
import com.example.leafly.ui.theme.LeaflyGreenLight
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    plantViewModel: PlantViewModel,
    onAddPlantClick: () -> Unit,
    onPlantClick: (String) -> Unit
) {
    val plants by plantViewModel.plants.collectAsState()
    val loading by plantViewModel.loading.collectAsState()
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var searchQuery by remember { mutableStateOf("") }

    val filteredPlants = plants.filter {
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.species.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) plantViewModel.getPlantsByUser(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Plants", fontWeight = FontWeight.Bold, color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LeaflyGreenDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPlantClick, containerColor = LeaflyGreenDark, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = "Add Plant", tint = Color.White)
            }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                placeholder = { Text("Search plants...", color = Color.LightGray) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = LeaflyGreenDark.copy(alpha = 0.5f)) },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = LeaflyGreenDark,
                    unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                ),
                singleLine = true
            )

            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = LeaflyGreenDark)
                }
            } else if (filteredPlants.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🌱", fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isEmpty()) "No plants yet" else "No plants found",
                            fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isEmpty()) "Tap + to add your first plant" else "Try a different search",
                            fontSize = 14.sp, color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredPlants) { plant ->
                        PlantItem(
                            plant = plant,
                            onClick = { onPlantClick(plant.id) },
                            onDelete = {
                                plantViewModel.deletePlant(plant.id) { success, _ ->
                                    if (success) plantViewModel.getPlantsByUser(userId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PlantItem(plant: PlantModel, onClick: () -> Unit, onDelete: () -> Unit) {
    val imageBitmap = remember(plant.imageUrl) {
        if (plant.imageUrl.isNotEmpty()) {
            try {
                val bytes = Base64.decode(plant.imageUrl, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) { null }
        } else null
    }

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LeaflyGreenLight.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Plant image
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(LeaflyGreenCircle),
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
                    Text("🌿", fontSize = 28.sp)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(plant.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                Text(plant.species, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Text("💧 Every ${plant.wateringFrequencyDays} days", fontSize = 13.sp, color = LeaflyGreenDark.copy(alpha = 0.7f))
                if (plant.sunlight.isNotEmpty()) {
                    Text("☀️ ${plant.sunlight}", fontSize = 13.sp, color = LeaflyGreenDark.copy(alpha = 0.7f))
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
            }
        }
    }
}
