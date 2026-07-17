package com.example.leafly

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToLogin() {
        composeTestRule.onNodeWithText("i already have an account").performClick()
    }

    @Test
    fun emptyEmail_showsError() {
        navigateToLogin()
        composeTestRule.onNodeWithText("sign in").performClick()
        composeTestRule.onNodeWithText("Please fill in all fields").assertIsDisplayed()
    }

    @Test
    fun emptyPassword_showsError() {
        navigateToLogin()
        composeTestRule.onNodeWithText("email address").performTextInput("test@test.com")
        composeTestRule.onNodeWithText("sign in").performClick()
        composeTestRule.onNodeWithText("Please fill in all fields").assertIsDisplayed()
    }

    @Test
    fun loginScreen_hasGoogleSignInButton() {
        navigateToLogin()
        composeTestRule.onNodeWithText("Sign in with Google").assertIsDisplayed()
    }

    @Test
    fun signUpLink_navigatesToRegister() {
        navigateToLogin()
        composeTestRule.onNodeWithText("sign up").performClick()
        composeTestRule.onNodeWithText("create account").assertIsDisplayed()
    }

    @Test
    fun forgotPassword_showsEmailField() {
        navigateToLogin()
        composeTestRule.onNodeWithText("forgot password?").performClick()
        composeTestRule.onNodeWithText("send reset link").assertIsDisplayed()
    }
}
