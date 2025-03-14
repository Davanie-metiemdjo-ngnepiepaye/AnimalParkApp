package com.animalpark.app.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@Composable
fun EnclosureDetailScreen(enclosureName: String, db: FirebaseDatabase, auth: FirebaseAuth) {
    var reviews by remember { mutableStateOf(listOf<Pair<String, Review>>()) }
    var rating by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    val context = LocalContext.current
    val currentUser = auth.currentUser

    // ✅ Charger les avis depuis Firebase pour l'enclos sélectionné
    LaunchedEffect(enclosureName) {
        val enclosureRef = db.reference.child("enclosures").child(enclosureName)
        val reviewRef = enclosureRef.child("reviews")

        reviewRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviewList = mutableListOf<Pair<String, Review>>()
                for (reviewSnapshot in snapshot.children) {
                    val reviewId = reviewSnapshot.key ?: continue
                    val review = reviewSnapshot.getValue(Review::class.java)
                    if (review != null) {
                        reviewList.add(reviewId to review)
                    }
                }
                reviews = reviewList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erreur de lecture", error.toException())
            }
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "🦁 Enclos : $enclosureName", style = MaterialTheme.typography.h5)

        // ✅ Champ pour entrer une note et un commentaire
        TextField(
            value = rating,
            onValueChange = { rating = it },
            label = { Text("Note (1 à 5)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = comment,
            onValueChange = { comment = it },
            label = { Text("Commentaire") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ✅ Bouton pour ajouter un avis
        Button(
            onClick = {
                val userId = currentUser?.uid ?: return@Button
                val newReview = Review(userId, rating.toIntOrNull() ?: 0, comment)

                db.reference.child("enclosures").child(enclosureName).child("reviews").push()
                    .setValue(newReview)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Avis ajouté", Toast.LENGTH_SHORT).show()
                        rating = "" // Réinitialiser les champs
                        comment = ""
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Soumettre l'avis")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ✅ Affichage des avis
        Text(text = "Avis des visiteurs", style = MaterialTheme.typography.h6)
        LazyColumn {
            items(reviews) { reviewPair ->
                val reviewId = reviewPair.first
                val review = reviewPair.second

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "⭐ Note : ${review.rating}/5", style = MaterialTheme.typography.body1)
                        Text(text = "💬 ${review.comment}", style = MaterialTheme.typography.body2)

                        // ✅ Bouton de suppression si l'utilisateur est l'auteur de l'avis
                        if (review.userId == currentUser?.uid) {
                            Button(
                                onClick = {
                                    db.reference.child("enclosures").child(enclosureName)
                                        .child("reviews").child(reviewId).removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Avis supprimé", Toast.LENGTH_SHORT).show()
                                            reviews = reviews.filterNot { it.first == reviewId }
                                        }
                                },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Supprimer mon avis")
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Review(
    val userId: String = "",
    val rating: Int = 0,
    val comment: String = ""
)
