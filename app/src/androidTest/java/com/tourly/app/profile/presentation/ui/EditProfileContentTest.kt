package com.tourly.app.profile.presentation.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.assertIsDisplayed
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.theme.TourlyTheme
import com.tourly.app.login.domain.UserRole
import com.tourly.app.profile.presentation.state.EditProfileUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditProfileContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun editProfileContent_rendersCorrectlyForTraveler() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val personalStr = appContext.getString(R.string.personal)
        val contactStr = appContext.getString(R.string.contact)
        val saveStr = appContext.getString(R.string.save)

        val state = EditProfileUiState(
            fullName = "John Doe",
            email = "john@example.com",
            bio = "Travel enthusiast",
            userRole = UserRole.TRAVELER
        )

        composeTestRule.setContent {
            TourlyTheme {
                EditProfileContent(
                    state = state,
                    onFullNameChange = {},
                    onEmailChange = {},
                    onBioChange = {},
                    onCertificationsChange = {},
                    onProfilePictureSelected = {},
                    onSaveClick = {}
                )
            }
        }

        // Verify section titles and save button
        composeTestRule.onNodeWithText(personalStr).assertIsDisplayed()
        composeTestRule.onNodeWithText(contactStr).assertIsDisplayed()
        composeTestRule.onNodeWithText(saveStr).assertIsDisplayed()

        // Verify fields are pre-filled
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed()
        composeTestRule.onNodeWithText("john@example.com").assertIsDisplayed()
        composeTestRule.onNodeWithText("Travel enthusiast").assertIsDisplayed()
    }

    @Test
    fun editProfileContent_rendersCertificationsForGuide() {
        val state = EditProfileUiState(
            fullName = "Guide Bob",
            email = "bob@example.com",
            userRole = UserRole.GUIDE,
            certifications = "Certified Mountain Guide"
        )

        composeTestRule.setContent {
            TourlyTheme {
                EditProfileContent(
                    state = state,
                    onFullNameChange = {},
                    onEmailChange = {},
                    onBioChange = {},
                    onCertificationsChange = {},
                    onProfilePictureSelected = {},
                    onSaveClick = {}
                )
            }
        }

        // Verify certifications field is visible
        composeTestRule.onNodeWithText("Certified Mountain Guide").assertIsDisplayed()
    }

    @Test
    fun editProfileContent_inputCallbacksTriggered() {
        var newFullName = ""
        var newBio = ""

        val state = EditProfileUiState(
            fullName = "Old Name",
            bio = "Old Bio",
            userRole = UserRole.TRAVELER
        )

        composeTestRule.setContent {
            TourlyTheme {
                EditProfileContent(
                    state = state,
                    onFullNameChange = { newFullName = it },
                    onEmailChange = {},
                    onBioChange = { newBio = it },
                    onCertificationsChange = {},
                    onProfilePictureSelected = {},
                    onSaveClick = {}
                )
            }
        }

        // Replace full name (performTextReplacement atomically replaces all existing text)
        composeTestRule.onNodeWithText("Old Name").performTextReplacement("New Name")
        assertEquals("New Name", newFullName)

        // Replace bio
        composeTestRule.onNodeWithText("Old Bio").performTextReplacement("New Bio")
        assertEquals("New Bio", newBio)
    }

    @Test
    fun editProfileContent_saveButtonClickTriggersCallback() {
        var saveClicked = false

        val state = EditProfileUiState(
            fullName = "John Doe",
            userRole = UserRole.TRAVELER
        )

        composeTestRule.setContent {
            TourlyTheme {
                EditProfileContent(
                    state = state,
                    onFullNameChange = {},
                    onEmailChange = {},
                    onBioChange = {},
                    onCertificationsChange = {},
                    onProfilePictureSelected = {},
                    onSaveClick = { saveClicked = true }
                )
            }
        }

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val saveStr = appContext.getString(R.string.save)

        // Click save button
        composeTestRule.onNodeWithText(saveStr).performClick()
        assertTrue(saveClicked)
    }
}
