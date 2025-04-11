package com.animalpark.app.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun AddReviewScreen(
    enclosureId: String,
    db: FirebaseDatabase,
    auth: FirebaseAuth,
    navController: NavController
) {
    var rating by remember { mutableStateOf(0) }
    var comment by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier.height(72.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.padding(top = 4.dp)) {
                            Text(
                                text = "📝 LAISSER UN AVIS",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Quelle note donnez-vous à cet enclos ?",
                style = MaterialTheme.typography.h6,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // ⭐ Sélecteur d’étoiles
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                        contentDescription = "Étoile $i",
                        tint = if (i <= rating) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface.copy(alpha = 0.4f),
                        modifier = Modifier
                            .size(36.dp)
                            .clickable { rating = i }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Votre commentaire") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val user = auth.currentUser
                    val userId = user?.uid ?: return@Button
                    val userEmail = user.email ?: "Utilisateur anonyme"

                    if (rating == 0 || comment.isBlank()) {
                        Toast.makeText(context, "Merci de noter et commenter l’enclos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val review = hashMapOf(
                        "userId" to userId,
                        "userEmail" to userEmail,
                        "rating" to rating,
                        "comment" to comment
                    )

                    db.reference
                        .child("comment")
                        .child("enclosures")
                        .child(enclosureId)
                        .child("comments")
                        .push()
                        .setValue(review)
                        .addOnSuccessListener {
                            Toast.makeText(context, "✅ Avis ajouté avec succès", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "❌ Erreur: ${it.message}", Toast.LENGTH_SHORT).show()
                        }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Soumettre l'avis", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
