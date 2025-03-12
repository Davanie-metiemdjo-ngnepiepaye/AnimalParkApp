package com.animalpark.app.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun NavigationScreen(startLatitude: Double, startLongitude: Double, endLatitude: Double, endLongitude: Double) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Navigation", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/?api=1&origin=$startLatitude,$startLongitude&destination=$endLatitude,$endLongitude&travelmode=walking")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
        }) {
            Text("🚶‍♂️ Démarrer la Navigation")
        }
    }
}
