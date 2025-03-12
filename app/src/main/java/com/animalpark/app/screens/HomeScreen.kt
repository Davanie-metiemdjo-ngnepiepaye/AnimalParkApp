package com.animalpark.app.screens

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
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenue dans l'application ZooApp", style = MaterialTheme.typography.h5)

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Bouton pour voir la liste des enclos
        Button(onClick = {
            navController.navigate("enclosures") // Naviguer vers l'écran des enclos
        }) {
            Text("Voir les enclos")
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
            Text("Se déconnecter")
        }
    }
}
