package com.tourly.app.profile.presentation.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import com.tourly.app.R
import com.tourly.app.core.presentation.ui.components.ImageCropperDialog
import com.tourly.app.core.presentation.ui.components.UserAvatar
import com.tourly.app.core.presentation.ui.theme.OutfitFamily
import com.tourly.app.profile.presentation.state.EditProfileUiState
import com.tourly.app.profile.presentation.ui.components.EditProfileTextField
import com.tourly.app.login.presentation.ui.components.FullNameTextField

@Composable
fun EditProfileContent(
    state: EditProfileUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onCertificationsChange: (String) -> Unit,
    onProfilePictureSelected: (Uri) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    var pendingImageUri by remember { mutableStateOf<Uri?>(null) }

    if (pendingImageUri != null) {
        ImageCropperDialog(
            imageUri = pendingImageUri!!,
            onCrop = { croppedUri ->
                onProfilePictureSelected(croppedUri)
                pendingImageUri = null
            },
            onDismiss = {
                pendingImageUri = null
            }
        )
    }
    
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> 
            if (uri != null) {
                pendingImageUri = uri
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Profile Picture
        Box(
            modifier = Modifier.clickable {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        ) {
            UserAvatar(
                imageUrl = (state.profilePictureUri ?: state.profilePictureUrl)?.toString(),
                name = state.fullName,
                textStyle = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .size(120.dp)
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.secondary,
                        shape = CircleShape
                    )
            )
            
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(id = R.string.edit_photo),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.surface, CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(6.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Personal Section
        Text(
            text = stringResource(id = R.string.personal),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = OutfitFamily,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        FullNameTextField(
            value = state.fullName,
            onValueChange = onFullNameChange
        )

        if (state.fullNameError != null) {
            Text(
                text = state.fullNameError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        EditProfileTextField(
            value = state.bio,
            onValueChange = onBioChange,
            label = stringResource(id = R.string.bio),
            isError = state.bioError != null,
            supportingText = state.bioError,
            minLines = 3,
            maxLines = 5,
            singleLine = false,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditProfileTextField(
            value = state.certifications,
            onValueChange = onCertificationsChange,
            label = stringResource(id = R.string.certifications),
            isError = state.certificationsError != null,
            supportingText = state.certificationsError,
            minLines = 2,
            maxLines = 4,
            singleLine = false,
            keyboardOptions = KeyboardOptions(
                autoCorrectEnabled = false
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Email & Password Section
        Text(
            text = stringResource(id = R.string.contact),
            style = MaterialTheme.typography.titleLarge,
            fontFamily = OutfitFamily,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        EditProfileTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = stringResource(id = R.string.email),
            isError = state.emailError != null,
            supportingText = state.emailError,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                autoCorrectEnabled = false,
                imeAction = ImeAction.Next
            )
        )



        if (state.saveError != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = state.saveError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = OutfitFamily
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (state.isSaving) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.save),
                    fontFamily = OutfitFamily
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}