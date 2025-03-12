package com.animalpark.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.animalpark.app.screens.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialisation de Firebase
        val auth = FirebaseAuth.getInstance()
        val db = FirebaseDatabase.getInstance()

        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = if (auth.currentUser != null) "home" else "login") {

                // Écran de connexion
                composable("login") { LoginScreen(navController, auth) }

                // Écran d'inscription
                composable("register") { RegisterScreen(navController, auth, db) }

                // Écran d'accueil après connexion
                composable("home") { HomeScreen(navController, auth) }

                // Écran affichant la liste des enclos et animaux
                composable("enclosures") { EnclosureListScreen(navController, db) }

                // Écran d'affichage des détails d'un enclos
                composable("enclosureDetail/{enclosureId}") { backStackEntry ->
                    val enclosureId = backStackEntry.arguments?.getString("enclosureId") ?: ""
                    EnclosureDetailScreen(enclosureId, db, auth)
                }

                // Écran d'ajout d'avis pour un enclos spécifique
                composable("addReview/{enclosureId}") { backStackEntry ->
                    val enclosureId = backStackEntry.arguments?.getString("enclosureId") ?: ""
                    AddReviewScreen(enclosureId, db, auth)

                    composable("navigate/{startLat}/{startLng}/{endLat}/{endLng}") { backStackEntry ->
                        val startLat = backStackEntry.arguments?.getString("startLat")?.toDoubleOrNull() ?: 0.0
                        val startLng = backStackEntry.arguments?.getString("startLng")?.toDoubleOrNull() ?: 0.0
                        val endLat = backStackEntry.arguments?.getString("endLat")?.toDoubleOrNull() ?: 0.0
                        val endLng = backStackEntry.arguments?.getString("endLng")?.toDoubleOrNull() ?: 0.0
                        NavigationScreen(startLat, startLng, endLat, endLng)
                    }

                }
            }
        }
    }
}