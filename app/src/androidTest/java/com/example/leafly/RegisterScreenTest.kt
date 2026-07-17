package com.example.leafly

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RegisterScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private fun navigateToRegister() {
        composeTestRule.onNodeWithText("get started").performClick()
    }

    @Test
    fun emptyFields_showsError() {
        navigateToRegister()
        composeTestRule.onNodeWithText("create account").performClick()
        composeTestRule.onNodeWithText("Please fill in all fields").assertIsDisplayed()
    }

    @Test
    fun passwordMismatch_showsError() {
        navigateToRegister()
        composeTestRule.onNodeWithText("full name").performTextInput("Test User")
        composeTestRule.onNodeWithText("email address").performTextInput("test@test.com")

        // Find password fields by index
        val passwordFields = composeTestRule.onAllNodesWithText("password")
        passwordFields[0].performTextInput("password123")
        passwordFields[1].performTextInput("differentpassword")

        composeTestRule.onNodeWithText("create account").performClick()
        composeTestRule.onNodeWithText("Passwords do not match").assertIsDisplayed()
    }

    @Test
    fun shortPassword_showsError() {
        navigateToRegister()
        composeTestRule.onNodeWithText("full name").performTextInput("Test User")
        composeTestRule.onNodeWithText("email address").performTextInput("test@test.com")

        val passwordFields = composeTestRule.onAllNodesWithText("password")
        passwordFields[0].performTextInput("123")
        passwordFields[1].performTextInput("123")

        composeTestRule.onNodeWithText("create account").performClick()
        composeTestRule.onNodeWithText("Password must be at least 6 characters").assertIsDisplayed()
    }

    @Test
    fun termsNotChecked_showsError() {
        navigateToRegister()
        composeTestRule.onNodeWithText("full name").performTextInput("Test User")
        composeTestRule.onNodeWithText("email address").performTextInput("test@test.com")

        val passwordFields = composeTestRule.onAllNodesWithText("password")
        passwordFields[0].performTextInput("password123")
        passwordFields[1].performTextInput("password123")

        composeTestRule.onNodeWithText("create account").performClick()
        composeTestRule.onNodeWithText("Please agree to terms and privacy policy").assertIsDisplayed()
    }

    @Test
    fun signInLink_navigatesToLogin() {
        navigateToRegister()
        composeTestRule.onNodeWithText("sign in").performClick()
        composeTestRule.onNodeWithText("welcome back").assertIsDisplayed()
    }
}
