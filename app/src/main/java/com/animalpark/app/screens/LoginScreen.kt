package com.animalpark.app.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.animalpark.app.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(navController: NavController, auth: FirebaseAuth) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Image de fond
        Image(
            painter = painterResource(id = R.drawable.zoo),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Contenu par-dessus l'image
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .align(Alignment.Center)
                .background(MaterialTheme.colors.surface.copy(alpha = 0.85f), shape = MaterialTheme.shapes.medium)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Connexion", style = MaterialTheme.typography.h5)

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Mot de passe") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true
            )

            Button(onClick = {
                if (email.isBlank() || password.isBlank()) {
                    Toast.makeText(context, "Veuillez entrer un email et un mot de passe", Toast.LENGTH_SHORT).show()
                } else {
                    auth.signInWithEmailAndPassword(email.trim(), password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Connexion réussie", Toast.LENGTH_SHORT).show()
                                navController.navigate("home")
                            } else {
                                Toast.makeText(context, "Email ou mot de passe incorrect", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }) {
                Text("Se connecter")
            }

            Text(
                text = "Pas encore de compte ? S'inscrire",
                fontSize = 14.sp,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.clickable {
                    navController.navigate("register")
                }
            )

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
}
