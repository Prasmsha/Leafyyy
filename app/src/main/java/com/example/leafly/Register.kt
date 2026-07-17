package com.example.leafly

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.leafly.Repo.UserRepo
import com.example.leafly.Repo.UserRepoImp
import com.example.leafly.ViewModel.UserViewModel
import com.example.leafly.model.UserModel
import com.example.leafly.ui.theme.LeaflyGreenDark
import com.example.leafly.ui.theme.LeaflyGreenLight
import com.example.leafly.ui.theme.LeaflyTheme

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    userViewModel: UserViewModel,
    onRegisterClick: () -> Unit = {},
    onSignInClick: () -> Unit = {}
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var agreeToTerms by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_leaf),
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = LeaflyGreenDark
                    )
                    Text(
                        text = "Leafly",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = LeaflyGreenDark
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "create account",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = LeaflyGreenDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "start your plant journey",
                    fontSize = 20.sp,
                    color = LeaflyGreenDark.copy(alpha = 0.8f)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = "full name", color = Color.LightGray)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_person),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = LeaflyGreenDark.copy(alpha = 0.5f)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LeaflyGreenDark,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = "email address", color = Color.LightGray)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_mail),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = LeaflyGreenDark.copy(alpha = 0.5f)
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LeaflyGreenDark,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = "password", color = Color.LightGray)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = LeaflyGreenDark.copy(alpha = 0.5f)
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LeaflyGreenDark,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(text = "confirm password", color = Color.LightGray)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_lock),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = LeaflyGreenDark.copy(alpha = 0.5f)
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = LeaflyGreenDark,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Checkbox(
                        checked = agreeToTerms,
                        onCheckedChange = { agreeToTerms = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = LeaflyGreenDark,
                            uncheckedColor = Color.LightGray.copy(alpha = 0.5f),
                            checkmarkColor = Color.White
                        ),
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Gray)) {
                                append("i agree to the ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = LeaflyGreenDark,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("terms")
                            }
                            withStyle(style = SpanStyle(color = Color.Gray)) {
                                append(" and ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = LeaflyGreenDark,
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append("privacy policy")
                            }
                        },
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (errorMessage.isNotEmpty()) {
                    Text(
                        text = errorMessage,
                        color = Color.Red,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (fullName.isBlank() || email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
                            errorMessage = "Please fill in all fields"
                            return@Button
                        }
                        if (password != confirmPassword) {
                            errorMessage = "Passwords do not match"
                            return@Button
                        }
                        if (password.length < 6) {
                            errorMessage = "Password must be at least 6 characters"
                            return@Button
                        }
                        if (!agreeToTerms) {
                            errorMessage = "Please agree to terms and privacy policy"
                            return@Button
                        }
                        isLoading = true
                        errorMessage = ""
                        userViewModel.register(email, password) { success, message, uid ->
                            if (success) {
                                val user = UserModel(
                                    id = uid,
                                    name = fullName,
                                    email = email
                                )
                                userViewModel.addUser(uid, user) { saved, saveMessage ->
                                    isLoading = false
                                    if (saved) {
                                        onRegisterClick()
                                    } else {
                                        errorMessage = saveMessage
                                    }
                                }
                            } else {
                                isLoading = false
                                errorMessage = message
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LeaflyGreenDark),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text(
                            text = "create account",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                TextButton(onClick = onSignInClick) {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(style = SpanStyle(color = Color.Gray)) {
                                append("already have an account? ")
                            }
                            withStyle(
                                style = SpanStyle(
                                    color = LeaflyGreenDark,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append("sign in")
                            }
                        },
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    // Create a fake repository for the preview to avoid Firebase initialization issues
    val fakeRepo = object : UserRepo {
        override fun login(email: String, password: String, callback: (Boolean, String) -> Unit) {}
        override fun forgetPassword(email: String, callback: (Boolean, String) -> Unit) {}
        override fun getUserById(id: String, callback: (Boolean, String, UserModel?) -> Unit) {}
        override fun getAllUser(callback: (Boolean, String, List<UserModel?>) -> Unit) {}
        override fun logout(callback: (Boolean, String) -> Unit) {}
        override fun register(email: String, password: String, callback: (Boolean, String, String) -> Unit) {}
        override fun addUser(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {}
        override fun editProfile(id: String, model: UserModel, callback: (Boolean, String) -> Unit) {}
        override fun deleteUser(id: String, callback: (Boolean, String) -> Unit) {}
    }
    LeaflyTheme {
        val viewModel = remember { UserViewModel(fakeRepo) }
        RegisterScreen(userViewModel = viewModel)
    }
}