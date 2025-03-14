package com.animalpark.app.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(navController: NavController, auth: FirebaseAuth) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "🏛 Bienvenue dans l'application ZooApp", style = MaterialTheme.typography.h5)

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Bouton pour voir la liste des enclos
        Button(onClick = {
            navController.navigate("enclosures") // Naviguer vers l'écran des enclos
        }) {
            Text("🦁 Voir les enclos")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Bouton pour voir les services du parc
        Button(onClick = {
            navController.navigate("services") // Naviguer vers l'écran des services
        }) {
            Text("🏪 Voir les services")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Bouton pour la navigation dans le parc (Google Maps)
        Button(onClick = {
            val gmmIntentUri = Uri.parse("geo:0,0?q=Parc Animalier")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            context.startActivity(mapIntent)
        }) {
            Text("🗺️ Naviguer dans le parc")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Bouton de déconnexion
        Button(onClick = {
            auth.signOut() // Déconnecter l'utilisateur
            Toast.makeText(context, "Déconnexion réussie", Toast.LENGTH_SHORT).show()
            navController.navigate("login") {
                popUpTo("home") { inclusive = true } // Effacer l'historique pour éviter de revenir en arrière
            }
        }) {
            Text("🚪 Se déconnecter")
        }
    }
}
