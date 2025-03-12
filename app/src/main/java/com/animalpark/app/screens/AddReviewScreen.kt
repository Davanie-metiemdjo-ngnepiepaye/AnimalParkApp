package com.animalpark.app.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

@Composable
fun AddReviewScreen(enclosureId: String, db: FirebaseDatabase, auth: FirebaseAuth) {
    var rating by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Ajouter un avis", style = MaterialTheme.typography.h5)

        TextField(value = rating, onValueChange = { rating = it }, label = { Text("Note (1 à 5)") })
        TextField(value = comment, onValueChange = { comment = it }, label = { Text("Commentaire") })

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val userId = auth.currentUser?.uid ?: return@Button
            if (rating.isBlank() || comment.isBlank()) {
                Toast.makeText(context, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@Button
            }
            val review = hashMapOf(
                "userId" to userId,
                "rating" to rating.toInt(),
                "comment" to comment
            )

            db.reference.child("enclosures").child(enclosureId).child("reviews").push()
                .setValue(review)
                .addOnSuccessListener {
                    Toast.makeText(context, "Avis ajouté avec succès", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Erreur: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }) {
            Text("Soumettre l'avis")
        }
    }
}
