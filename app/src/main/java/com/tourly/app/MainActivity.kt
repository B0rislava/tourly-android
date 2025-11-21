package com.tourly.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tourly.app.core.ui.theme.OutfitFamily
import com.tourly.app.core.ui.theme.TourlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TourlyTheme {
                    Greeting(name = "Android")
                }
            }
        }
    }

@Composable
fun Greeting(name: String) {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Hello $name!",
            fontFamily = OutfitFamily,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}


@Preview(name = "Light Mode")
@Composable
fun GreetingLightPreview() {
    TourlyTheme(darkTheme = false) {
        Greeting("Android")
    }
}

@Preview(name = "Dark Mode")
@Composable
fun GreetingDarkPreview() {
    TourlyTheme(darkTheme = true) {
        Greeting("Android")
    }
}
