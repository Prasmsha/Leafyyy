package com.example.leafly

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leafly.ViewModel.UserViewModel
import com.example.leafly.model.UserModel
import com.example.leafly.ui.theme.LeaflyGreenCircle
import com.example.leafly.ui.theme.LeaflyGreenDark
import com.example.leafly.ui.theme.LeaflyGreenLight
import com.google.firebase.auth.FirebaseAuth
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userViewModel: UserViewModel,
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onBackClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val currentUser = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current
    val userState by userViewModel.users.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var contact by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var avatarBase64 by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }
    var isProcessingImage by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var showDeleteAccountDialog by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            isProcessingImage = true
            message = ""
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val originalBitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                val maxSize = 200
                val ratio = minOf(maxSize.toFloat() / originalBitmap.width, maxSize.toFloat() / originalBitmap.height)
                val resized = Bitmap.createScaledBitmap(originalBitmap, (originalBitmap.width * ratio).toInt(), (originalBitmap.height * ratio).toInt(), true)
                val out = ByteArrayOutputStream()
                resized.compress(Bitmap.CompressFormat.JPEG, 70, out)
                avatarBase64 = Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)
                isProcessingImage = false
                message = "Photo ready! Tap Save Profile to keep it. ✅"
            } catch (e: Exception) {
                isProcessingImage = false
                message = "Failed to process image"
            }
        }
    }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) userViewModel.getUserById(userId)
    }

    LaunchedEffect(userState) {
        userState?.let { user ->
            name = user.name
            if (user.email.isNotEmpty()) email = user.email
            contact = user.contact
            address = user.address
            avatarBase64 = user.avatarUrl
        }
    }

    val avatarBitmap = remember(avatarBase64) {
        if (avatarBase64.isNotEmpty()) {
            try {
                val bytes = Base64.decode(avatarBase64, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            } catch (e: Exception) { null }
        } else null
    }

    val bgColor = if (isDarkTheme) Color(0xFF121212) else Color.White
    val textColor = if (isDarkTheme) Color.White else Color(0xFF1C1B1F)
    val cardColor = if (isDarkTheme) Color(0xFF2A2A2A) else LeaflyGreenLight.copy(alpha = 0.3f)

    // Delete account confirmation dialog
    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Delete Account", fontWeight = FontWeight.Bold, color = Color.Red) },
            text = { Text("Are you sure you want to permanently delete your account? This action cannot be undone and all your data will be lost.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        // Delete user data from Realtime Database
                        userViewModel.deleteUser(userId) { _, _ -> }
                        // Delete Firebase Auth account
                        currentUser?.delete()?.addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                GoogleSignInHelper.signOut(context)
                                onLogoutClick()
                            } else {
                                message = "Failed to delete account: ${task.exception?.message}"
                            }
                        }
                    }
                ) {
                    Text("Delete Forever", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancel", color = LeaflyGreenDark)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = LeaflyGreenDark)
            )
        },
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar header
            Column(
                modifier = Modifier.fillMaxWidth().background(LeaflyGreenLight).padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isProcessingImage) {
                    Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(LeaflyGreenCircle), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LeaflyGreenDark, modifier = Modifier.size(32.dp))
                    }
                } else if (avatarBitmap != null) {
                    Image(
                        bitmap = avatarBitmap.asImageBitmap(),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(100.dp).clip(CircleShape)
                            .border(3.dp, LeaflyGreenDark, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier.size(100.dp).clip(CircleShape)
                            .background(LeaflyGreenCircle)
                            .border(3.dp, LeaflyGreenDark, CircleShape)
                            .clickable { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (name.isNotEmpty()) name[0].uppercaseChar().toString()
                                   else if (email.isNotEmpty()) email[0].uppercaseChar().toString()
                                   else "?",
                            fontSize = 36.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("tap photo to change", fontSize = 12.sp, color = LeaflyGreenDark.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(8.dp))
                Text(name.ifEmpty { email.substringBefore("@") }, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                Text(email, fontSize = 14.sp, color = LeaflyGreenDark.copy(alpha = 0.7f))
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Dark mode toggle
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = cardColor)) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = if (isDarkTheme) "🌙 Dark Mode" else "☀️ Light Mode",
                            fontSize = 16.sp, fontWeight = FontWeight.Medium, color = textColor
                        )
                        Switch(
                            checked = isDarkTheme, onCheckedChange = { onToggleTheme() },
                            colors = SwitchDefaults.colors(checkedThumbColor = LeaflyGreenDark, checkedTrackColor = LeaflyGreenLight)
                        )
                    }
                }

                LeaflyTextField(value = name, onValueChange = { name = it }, label = "Full Name")
                LeaflyTextField(value = contact, onValueChange = { contact = it }, label = "Contact Number")
                LeaflyTextField(value = address, onValueChange = { address = it }, label = "Address", singleLine = false)

                if (message.isNotEmpty()) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (message.contains("failed") || message.contains("Failed"))
                                Color(0xFFFFEBEE) else Color(0xFFE8F5E9)
                        )
                    ) {
                        Text(
                            text = message,
                            color = if (message.contains("failed") || message.contains("Failed")) Color.Red else LeaflyGreenDark,
                            fontSize = 14.sp, modifier = Modifier.padding(12.dp)
                        )
                    }
                }

                // Save Profile Button
                Button(
                    onClick = {
                        if (name.isBlank()) { message = "Name cannot be empty"; return@Button }
                        isSaving = true; message = ""
                        val updatedUser = UserModel(id = userId, name = name, email = email, contact = contact, address = address, avatarUrl = avatarBase64)
                        userViewModel.editProfile(userId, updatedUser) { success, msg ->
                            isSaving = false
                            message = if (success) "Profile saved! ✅" else msg
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LeaflyGreenDark),
                    enabled = !isSaving && !isProcessingImage
                ) {
                    if (isSaving) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("Save Profile", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Logout Button
                OutlinedButton(
                    onClick = {
                        GoogleSignInHelper.signOut(context)
                        userViewModel.logout { _, _ -> onLogoutClick() }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                // Delete Account Button
                OutlinedButton(
                    onClick = { showDeleteAccountDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.Red.copy(alpha = 0.5f))
                ) {
                    Text("🗑️ Delete Account", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
