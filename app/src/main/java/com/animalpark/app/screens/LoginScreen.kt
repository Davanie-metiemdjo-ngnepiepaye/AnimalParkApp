package com.animalpark.app.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Connexion", style = MaterialTheme.typography.h5)

        Spacer(modifier = Modifier.height(16.dp))

        // Champ email
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Champ mot de passe
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mot de passe") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Bouton connexion
        Button(onClick = {
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(context, "Veuillez entrer un email et un mot de passe", Toast.LENGTH_SHORT).show()
            } else {
                auth.signInWithEmailAndPassword(email.trim(), password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Connexion réussie", Toast.LENGTH_SHORT).show()
                            navController.navigate("home") // Redirection vers l'écran d'accueil
                        } else {
                            Toast.makeText(context, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }) {
            Text("Se connecter")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lien pour s'inscrire si l'utilisateur n'a pas de compte
        Text(
            text = "Pas encore de compte ? S'inscrire",
            fontSize = 14.sp,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.clickable {
                navController.navigate("register")
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Lien pour réinitialiser le mot de passe
        Text(
            text = "Mot de passe oublié ?",
            fontSize = 14.sp,
            color = MaterialTheme.colors.secondary,
            modifier = Modifier.clickable {
                if (email.isBlank()) {
                    Toast.makeText(context, "Entrez votre email pour réinitialiser le mot de passe", Toast.LENGTH_SHORT).show()
                } else {
                    auth.sendPasswordResetEmail(email.trim())
                        .addOnSuccessListener {
                            Toast.makeText(context, "Email de réinitialisation envoyé", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Erreur: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        )
    }
}
