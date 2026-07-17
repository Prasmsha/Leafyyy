package com.example.leafly

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leafly.ViewModel.UserViewModel
import com.example.leafly.ui.theme.LeaflyGreenDark
import com.example.leafly.ui.theme.LeaflyGreenLight

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    onSignInClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        isGoogleLoading = true
        errorMessage = ""
        GoogleSignInHelper.handleSignInResult(
            data = result.data,
            onSuccess = {
                isGoogleLoading = false
                onSignInClick()
            },
            onFailure = { message ->
                isGoogleLoading = false
                errorMessage = message
            }
        )
    }

    Surface(modifier = modifier.fillMaxSize(), color = Color.White) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = LeaflyGreenLight,
                        shape = RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp)
                    )
                    .padding(top = 60.dp, bottom = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_leaf),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = LeaflyGreenDark
                    )
                    Text("Leafly", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text("welcome back", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = LeaflyGreenDark)
                Spacer(modifier = Modifier.height(8.dp))
                Text("sign in to your garden", fontSize = 20.sp, color = LeaflyGreenDark.copy(alpha = 0.8f))
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = email, onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("email address", color = Color.LightGray) },
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.ic_mail), contentDescription = null,
                            modifier = Modifier.size(24.dp), tint = LeaflyGreenDark.copy(alpha = 0.5f))
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LeaflyGreenDark,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = password, onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("password", color = Color.LightGray) },
                    leadingIcon = {
                        Icon(painter = painterResource(id = R.drawable.ic_lock), contentDescription = null,
                            modifier = Modifier.size(24.dp), tint = LeaflyGreenDark.copy(alpha = 0.5f))
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LeaflyGreenDark,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onForgotPasswordClick, modifier = Modifier.align(Alignment.End)) {
                    Text("forgot password?", color = LeaflyGreenDark, textDecoration = TextDecoration.Underline,
                        fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }

                if (errorMessage.isNotEmpty()) {
                    Text(text = errorMessage, color = Color.Red, fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (email.isBlank() || password.isBlank()) {
                            errorMessage = "Please fill in all fields"; return@Button
                        }
                        isLoading = true; errorMessage = ""
                        userViewModel.login(email, password) { success, message ->
                            isLoading = false
                            if (success) onSignInClick() else errorMessage = message
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(64.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LeaflyGreenDark),
                    enabled = !isLoading && !isGoogleLoading
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("sign in", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
                    Text("or continue with", modifier = Modifier.padding(horizontal = 16.dp),
                        color = LeaflyGreenDark.copy(alpha = 0.6f), fontSize = 14.sp)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray.copy(alpha = 0.5f))
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Google Sign-In Button
                OutlinedButton(
                    onClick = {
                        isGoogleLoading = true
                        errorMessage = ""
                        val signInIntent = GoogleSignInHelper.getSignInIntent(context)
                        googleSignInLauncher.launch(signInIntent)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LeaflyGreenDark),
                    enabled = !isLoading && !isGoogleLoading
                ) {
                    if (isGoogleLoading) {
                        CircularProgressIndicator(color = LeaflyGreenDark, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Image(
                                painter = painterResource(id = R.drawable.google),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp)
                            )
                            Text("Sign in with Google", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                TextButton(onClick = onSignUpClick) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Gray)) { append("don't have an account? ") }
                            withStyle(style = SpanStyle(color = LeaflyGreenDark, fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline)) { append("sign up") }
                        },
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
