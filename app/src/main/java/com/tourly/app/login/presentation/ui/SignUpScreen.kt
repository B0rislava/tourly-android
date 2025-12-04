package com.tourly.app.login.presentation.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tourly.app.R
import com.tourly.app.core.ui.components.AuthCard
import com.tourly.app.core.ui.components.AuthCardHeader
import com.tourly.app.core.ui.components.ClickableText
import com.tourly.app.core.ui.components.EmailTextField
import com.tourly.app.core.ui.components.FirstNameTextField
import com.tourly.app.core.ui.components.LastNameTextField
import com.tourly.app.core.ui.components.PasswordTextField
import com.tourly.app.core.ui.components.PrimaryButton
import com.tourly.app.core.ui.components.RoleSelector
import com.tourly.app.core.ui.components.UserRole
import com.tourly.app.core.ui.theme.TourlyTheme

@Composable
fun SignUpScreen() {

    var email by remember { mutableStateOf(value = "") }
    var password by remember { mutableStateOf(value = "") }
    var firstName by remember { mutableStateOf(value = "") }
    var lastName by remember { mutableStateOf(value = "") }
    var role by remember { mutableStateOf(value = UserRole.Traveler) }


    AuthCard {

        AuthCardHeader(
            title = stringResource(id = R.string.welcome_abroad),
            subtitle = stringResource(id = R.string.journey_starts)
        )

        Spacer(modifier = Modifier.height(height = 20.dp))


        RoleSelector(
            selectedRole = role,
            onRoleSelected = { role = it }
        )

        Spacer(modifier = Modifier.height(height = 10.dp))

        FirstNameTextField(
            value = firstName,
            onValueChange = { firstName = it }
        )

        Spacer(modifier = Modifier.height(height = 10.dp))

        LastNameTextField(
            value = lastName,
            onValueChange = { lastName = it }
        )

        Spacer(modifier = Modifier.height(height = 10.dp))

        EmailTextField(
            value = email,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.height(height = 10.dp))

        PasswordTextField(
            value = password,
            onValueChange = { password = it }
        )

        Spacer(modifier = Modifier.height(height = 20.dp))

        PrimaryButton(
            text = stringResource(id = R.string.register),
            onClick = { }
        )

        Spacer(modifier = Modifier.height(height = 8.dp))

        ClickableText(
            prefixText = stringResource(id = R.string.have_account),
            actionText = stringResource(id = R.string.login),
            onActionClick = { }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewSignUpScreen() {
    TourlyTheme {
        SignUpScreen()
    }
}
