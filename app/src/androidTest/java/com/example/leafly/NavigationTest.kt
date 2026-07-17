package com.example.leafly

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NavigationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun splashScreen_isDisplayed() {
        // Check splash screen elements are visible
        composeTestRule.onNodeWithText("your plants,").assertIsDisplayed()
    }

    @Test
    fun getStarted_navigatesToRegister() {
        composeTestRule.onNodeWithText("get started").performClick()
        composeTestRule.onNodeWithText("create account").assertIsDisplayed()
    }

    @Test
    fun alreadyHaveAccount_navigatesToLogin() {
        composeTestRule.onNodeWithText("i already have an account").performClick()
        composeTestRule.onNodeWithText("welcome back").assertIsDisplayed()
    }

    @Test
    fun loginScreen_hasEmailAndPasswordFields() {
        composeTestRule.onNodeWithText("i already have an account").performClick()
        composeTestRule.onNodeWithText("email address").assertIsDisplayed()
        composeTestRule.onNodeWithText("password").assertIsDisplayed()
    }

    @Test
    fun loginScreen_hasSignInButton() {
        composeTestRule.onNodeWithText("i already have an account").performClick()
        composeTestRule.onNodeWithText("sign in").assertIsDisplayed()
    }

    @Test
    fun loginScreen_hasForgotPasswordLink() {
        composeTestRule.onNodeWithText("i already have an account").performClick()
        composeTestRule.onNodeWithText("forgot password?").assertIsDisplayed()
    }

    @Test
    fun forgotPassword_navigatesCorrectly() {
        composeTestRule.onNodeWithText("i already have an account").performClick()
        composeTestRule.onNodeWithText("forgot password?").performClick()
        composeTestRule.onNodeWithText("reset password").assertIsDisplayed()
    }

    @Test
    fun registerScreen_hasRequiredFields() {
        composeTestRule.onNodeWithText("get started").performClick()
        composeTestRule.onNodeWithText("full name").assertIsDisplayed()
        composeTestRule.onNodeWithText("email address").assertIsDisplayed()
        composeTestRule.onNodeWithText("password").assertIsDisplayed()
    }

    @Test
    fun registerScreen_hasTermsCheckbox() {
        composeTestRule.onNodeWithText("get started").performClick()
        composeTestRule.onNodeWithText("terms").assertIsDisplayed()
    }
}
