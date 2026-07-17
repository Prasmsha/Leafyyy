package com.example.leafly

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leafly.ViewModel.PlantViewModel
import com.example.leafly.model.PlantModel
import com.example.leafly.ui.theme.LeaflyGreenCircle
import com.example.leafly.ui.theme.LeaflyGreenDark
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlantScreen(
    plantViewModel: PlantViewModel,
    onBackClick: () -> Unit,
    onPlantAdded: () -> Unit
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var species by remember { mutableStateOf("") }
    var wateringDays by remember { mutableStateOf("7") }
    var sunlight by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var imageBase64 by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isProcessingImage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") } // starts empty - no cached errors
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isProcessingImage = true
            errorMessage = ""
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                val maxSize = 300
                val ratio = minOf(
                    maxSize.toFloat() / originalBitmap.width,
                    maxSize.toFloat() / originalBitmap.height
                )
                val resized = Bitmap.createScaledBitmap(
                    originalBitmap,
                    (originalBitmap.width * ratio).toInt(),
                    (originalBitmap.height * ratio).toInt(),
                    true
                )
                val out = ByteArrayOutputStream()
                resized.compress(Bitmap.CompressFormat.JPEG, 70, out)
                imageBase64 = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)
                isProcessingImage = false
            } catch (e: Exception) {
                isProcessingImage = false
                errorMessage = "Failed to process image. Please try another."
            }
        }
    }

    val imageBitmap = remember(imageBase64) {
        if (imageBase64.isNotEmpty()) {
            try {
                val bytes = Base64.decode(imageBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) { null }
        } else null
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Plant", fontWeight = FontWeight.Bold, color = Color.White) },
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
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(LeaflyGreenCircle)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                when {
                    isProcessingImage -> CircularProgressIndicator(color = LeaflyGreenDark)
                    imageBitmap != null -> Image(
                        bitmap = imageBitmap.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    else -> Text("📷", fontSize = 40.sp)
                }
            }
            Text(
                "Tap to add image (optional)",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            LeaflyTextField(value = name, onValueChange = { name = it }, label = "Plant Name")
            Spacer(modifier = Modifier.height(16.dp))
            LeaflyTextField(value = species, onValueChange = { species = it }, label = "Species")
            Spacer(modifier = Modifier.height(16.dp))
            LeaflyTextField(
                value = wateringDays,
                onValueChange = { wateringDays = it },
                label = "Watering Frequency (days)",
                keyboardType = KeyboardType.Number
            )
            Spacer(modifier = Modifier.height(16.dp))
            LeaflyTextField(value = sunlight, onValueChange = { sunlight = it }, label = "Sunlight (e.g. Full Sun, Partial)")
            Spacer(modifier = Modifier.height(16.dp))
            LeaflyTextField(value = notes, onValueChange = { notes = it }, label = "Notes", singleLine = false)
            Spacer(modifier = Modifier.height(16.dp))

            if (errorMessage.isNotEmpty()) {
                Text(text = errorMessage, color = Color.Red, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Plant name is required"
                        return@Button
                    }
                    isLoading = true
                    errorMessage = ""
                    val plant = PlantModel(
                        userId = userId,
                        name = name,
                        species = species,
                        wateringFrequencyDays = wateringDays.toIntOrNull() ?: 7,
                        sunlight = sunlight,
                        notes = notes,
                        imageUrl = imageBase64
                    )
                    plantViewModel.addPlant(plant) { success, message ->
                        isLoading = false
                        if (success) {
                            WateringReminderScheduler.scheduleReminder(context, plant)
                            onPlantAdded()
                        } else {
                            errorMessage = message
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LeaflyGreenDark),
                enabled = !isLoading && !isProcessingImage
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                else Text("Add Plant", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}
