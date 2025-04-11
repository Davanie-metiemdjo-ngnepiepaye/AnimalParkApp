package com.animalpark.app.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun ServiceDetailScreen(serviceName: String, location: String, startPoint: String = "Entrée du Parc") {
    val context = LocalContext.current // Récupère le contexte pour lancer Google Maps

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "🏛 Détails du Service", style = MaterialTheme.typography.h5)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "📌 Nom : $serviceName")
        Text(text = "📍 Emplacement : $location")

        Spacer(modifier = Modifier.height(16.dp))

        // 🔵 Ouvrir la position dans Google Maps
        Button(onClick = {
            val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(location)} ($serviceName)")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }

            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                Toast.makeText(context, "Google Maps n'est pas installé", Toast.LENGTH_LONG).show()
            }
        }) {
            Text("📍 Voir sur Google Maps")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔴 Lancer un itinéraire entre "Entrée du Parc" et le service
        Button(onClick = {
            val gmmIntentUri = Uri.parse("https://www.google.com/maps/dir/${Uri.encode(startPoint)}/${Uri.encode(location)}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }

            if (mapIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(mapIntent)
            } else {
                Toast.makeText(context, "Google Maps n'est pas installé", Toast.LENGTH_LONG).show()
            }
        }) {
            Text("🚗 Itinéraire depuis l'Entrée du Parc")
        }
    }
}
