package com.animalpark.app.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.animalpark.app.R
import com.animalpark.app.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun RegisterScreen(navController: NavController, auth: FirebaseAuth, db: FirebaseDatabase) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Arrière-plan image
        Image(
            painter = painterResource(id = R.drawable.zoo),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Formulaire centré
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
            Text(text = "Créer un compte", style = MaterialTheme.typography.h5)

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true
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
                    Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                } else {
                    auth.createUserWithEmailAndPassword(email.trim(), password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = User(auth.currentUser!!.uid, email.trim())
                                db.reference.child("users").child(user.uid).setValue(user)
                                Toast.makeText(context, "Inscription réussie", Toast.LENGTH_SHORT).show()
                                navController.navigate("login")
                            } else {
                                Toast.makeText(context, "Erreur: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }) {
                Text("S'inscrire")
            }

            Text(
                text = "Déjà un compte ? Se connecter",
                fontSize = 14.sp,
                color = MaterialTheme.colors.primary,
                modifier = Modifier.clickable {
                    navController.navigate("login")
                }
            )
        }
    }
}
