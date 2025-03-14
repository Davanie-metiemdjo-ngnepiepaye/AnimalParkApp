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
                }
            }
        }
    }
}
