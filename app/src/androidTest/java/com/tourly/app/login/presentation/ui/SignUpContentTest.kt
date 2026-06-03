package com.tourly.app.login.presentation.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.theme.TourlyTheme
import com.tourly.app.login.domain.UserRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SignUpContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun signUpContent_rendersCorrectly() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val titleStr = appContext.getString(R.string.welcome_abroad)
        val registerStr = appContext.getString(R.string.register)

        composeTestRule.setContent {
            TourlyTheme {
                SignUpContent(
                    email = "",
                    onEmailChange = {},
                    password = "",
                    onPasswordChange = {},
                    confirmPassword = "",
                    onConfirmPasswordChange = {},
                    fullName = "",
                    onFullNameChange = {},
                    role = UserRole.TRAVELER,
                    onRoleChange = {},
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null,
                    fullNameError = null,
                    signUpError = null,
                    isLoading = false,
                    onRegisterClick = {},
                    onGoogleRegisterClick = {},
                    onLoginClick = {}
                )
            }
        }

        // Verify that the title and register button are displayed
        composeTestRule.onNodeWithText(titleStr).assertIsDisplayed()
        composeTestRule.onNodeWithText(registerStr).assertIsDisplayed()
    }

    @Test
    fun signUpContent_inputCallbacksTriggered() {
        var emailInput = ""
        var passwordInput = ""
        var confirmPasswordInput = ""
        var fullNameInput = ""

        composeTestRule.setContent {
            TourlyTheme {
                SignUpContent(
                    email = emailInput,
                    onEmailChange = { emailInput = it },
                    password = passwordInput,
                    onPasswordChange = { passwordInput = it },
                    confirmPassword = confirmPasswordInput,
                    onConfirmPasswordChange = { confirmPasswordInput = it },
                    fullName = fullNameInput,
                    onFullNameChange = { fullNameInput = it },
                    role = UserRole.TRAVELER,
                    onRoleChange = {},
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null,
                    fullNameError = null,
                    signUpError = null,
                    isLoading = false,
                    onRegisterClick = {},
                    onGoogleRegisterClick = {},
                    onLoginClick = {}
                )
            }
        }

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val fullNamePlaceholder = appContext.getString(R.string.enter_full_name)
        val emailPlaceholder = appContext.getString(R.string.enter_email)
        val newPasswordPlaceholder = appContext.getString(R.string.enter_new_password)
        val confirmPasswordPlaceholder = appContext.getString(R.string.enter_confirm_password)

        // Type full name
        composeTestRule.onNodeWithText(fullNamePlaceholder).performTextInput("John Doe")
        assertEquals("John Doe", fullNameInput)

        // Type email
        composeTestRule.onNodeWithText(emailPlaceholder).performTextInput("john@example.com")
        assertEquals("john@example.com", emailInput)

        // Type passwords
        composeTestRule.onNodeWithText(newPasswordPlaceholder).performTextInput("password123")
        assertEquals("password123", passwordInput)

        composeTestRule.onNodeWithText(confirmPasswordPlaceholder).performTextInput("password123")
        assertEquals("password123", confirmPasswordInput)
    }

    @Test
    fun signUpContent_registerButtonClickTriggersCallback() {
        var registerClicked = false

        composeTestRule.setContent {
            TourlyTheme {
                SignUpContent(
                    email = "test@example.com",
                    onEmailChange = {},
                    password = "password123",
                    onPasswordChange = {},
                    confirmPassword = "password123",
                    onConfirmPasswordChange = {},
                    fullName = "Test User",
                    onFullNameChange = {},
                    role = UserRole.TRAVELER,
                    onRoleChange = {},
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null,
                    fullNameError = null,
                    signUpError = null,
                    isLoading = false,
                    onRegisterClick = { registerClicked = true },
                    onGoogleRegisterClick = {},
                    onLoginClick = {}
                )
            }
        }

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val registerStr = appContext.getString(R.string.register)

        // Click register button
        composeTestRule.onNodeWithText(registerStr).performClick()
        assertTrue(registerClicked)
    }

    @Test
    fun signUpContent_whenLoading_buttonIsDisabled() {
        composeTestRule.setContent {
            TourlyTheme {
                SignUpContent(
                    email = "",
                    onEmailChange = {},
                    password = "",
                    onPasswordChange = {},
                    confirmPassword = "",
                    onConfirmPasswordChange = {},
                    fullName = "",
                    onFullNameChange = {},
                    role = UserRole.TRAVELER,
                    onRoleChange = {},
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null,
                    fullNameError = null,
                    signUpError = null,
                    isLoading = true,
                    onRegisterClick = {},
                    onGoogleRegisterClick = {},
                    onLoginClick = {}
                )
            }
        }

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val registerStr = appContext.getString(R.string.register)

        // Verify register button is disabled
        composeTestRule.onNodeWithText(registerStr).assertIsNotEnabled()
    }
}
