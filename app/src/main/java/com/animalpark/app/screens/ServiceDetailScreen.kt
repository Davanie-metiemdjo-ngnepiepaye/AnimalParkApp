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
fun ServiceDetailScreen(serviceName: String, latitude: Double, longitude: Double) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Détails du Service", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "📌 Nom : $serviceName")
        Text(text = "🌍 Latitude : $latitude")
        Text(text = "🌍 Longitude : $longitude")

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val gmmIntentUri = Uri.parse("geo:$latitude,$longitude?q=$latitude,$longitude($serviceName)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
        }) {
            Text("📍 Ouvrir dans Google Maps")
        }
    }
}
