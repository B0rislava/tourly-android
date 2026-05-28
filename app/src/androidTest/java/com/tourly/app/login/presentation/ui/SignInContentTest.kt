package com.tourly.app.login.presentation.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.theme.TourlyTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignInContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun signInContent_rendersCorrectly() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val welcomeBackStr = appContext.getString(R.string.welcome_back)
        val loginStr = appContext.getString(R.string.login)

        composeTestRule.setContent {
            TourlyTheme {
                SignInContent(
                    email = "",
                    onEmailChange = {},
                    password = "",
                    onPasswordChange = {},
                    emailError = null,
                    passwordError = null,
                    loginError = null,
                    isLoading = false,
                    onLoginClick = {},
                    onRegisterClick = {},
                    onGoogleLoginClick = {}
                )
            }
        }

        // Verify that the title and login button are displayed
        composeTestRule.onNodeWithText(welcomeBackStr).assertIsDisplayed()
        composeTestRule.onNodeWithText(loginStr).assertIsDisplayed()
    }

    @Test
    fun     signInContent_inputCallbacksTriggered() {
        var emailInput = ""
        var passwordInput = ""

        composeTestRule.setContent {
            TourlyTheme {
                SignInContent(
                    email = emailInput,
                    onEmailChange = { emailInput = it },
                    password = passwordInput,
                    onPasswordChange = { passwordInput = it },
                    emailError = null,
                    passwordError = null,
                    loginError = null,
                    isLoading = false,
                    onLoginClick = {},
                    onRegisterClick = {},
                    onGoogleLoginClick = {}
                )
            }
        }

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val emailPlaceholder = appContext.getString(R.string.enter_email)
        val passwordPlaceholder = appContext.getString(R.string.enter_password)

        // Type email - using placeholder to find the OutlinedTextField
        composeTestRule.onNodeWithText(emailPlaceholder).performTextInput("test@example.com")
        assertEquals("test@example.com", emailInput)

        // Type password
        composeTestRule.onNodeWithText(passwordPlaceholder).performTextInput("password123")
        assertEquals("password123", passwordInput)
    }

    @Test
    fun signInContent_loginButtonClickTriggersCallback() {
        var loginClicked = false

        composeTestRule.setContent {
            TourlyTheme {
                SignInContent(
                    email = "test@example.com",
                    onEmailChange = {},
                    password = "password123",
                    onPasswordChange = {},
                    emailError = null,
                    passwordError = null,
                    loginError = null,
                    isLoading = false,
                    onLoginClick = { loginClicked = true },
                    onRegisterClick = {},
                    onGoogleLoginClick = {}
                )
            }
        }

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val loginStr = appContext.getString(R.string.login)

        // Click login button
        composeTestRule.onNodeWithText(loginStr).performClick()
        assertTrue(loginClicked)
    }

    @Test
    fun signInContent_showsErrorMessages() {
        val emailErrorMsg = "Invalid email format"
        val passwordErrorMsg = "Password too short"
        val loginErrorMsg = "Invalid credentials"

        composeTestRule.setContent {
            TourlyTheme {
                SignInContent(
                    email = "",
                    onEmailChange = {},
                    password = "",
                    onPasswordChange = {},
                    emailError = emailErrorMsg,
                    passwordError = passwordErrorMsg,
                    loginError = loginErrorMsg,
                    isLoading = false,
                    onLoginClick = {},
                    onRegisterClick = {},
                    onGoogleLoginClick = {}
                )
            }
        }

        // Verify error texts are displayed
        composeTestRule.onNodeWithText(emailErrorMsg).assertIsDisplayed()
        composeTestRule.onNodeWithText(passwordErrorMsg).assertIsDisplayed()
        composeTestRule.onNodeWithText(loginErrorMsg).assertIsDisplayed()
    }

    @Test
    fun signInContent_whenLoading_buttonIsDisabled() {
        composeTestRule.setContent {
            TourlyTheme {
                SignInContent(
                    email = "",
                    onEmailChange = {},
                    password = "",
                    onPasswordChange = {},
                    emailError = null,
                    passwordError = null,
                    loginError = null,
                    isLoading = true,
                    onLoginClick = {},
                    onRegisterClick = {},
                    onGoogleLoginClick = {}
                )
            }
        }

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val loginStr = appContext.getString(R.string.login)

        // Verify login button is disabled
        composeTestRule.onNodeWithText(loginStr).assertIsNotEnabled()
    }
}
