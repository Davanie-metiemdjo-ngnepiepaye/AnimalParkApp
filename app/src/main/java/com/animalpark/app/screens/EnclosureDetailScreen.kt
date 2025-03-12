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
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

@Composable
fun EnclosureDetailScreen(enclosureName: String, db: FirebaseDatabase, auth: FirebaseAuth) {
    var reviews by remember { mutableStateOf(listOf<Pair<String, Review>>()) }
    var rating by remember { mutableStateOf("") }
    var comment by remember { mutableStateOf("") }
    val context = LocalContext.current
    val currentUser = auth.currentUser

    LaunchedEffect(Unit) {
        val reviewRef = db.reference.child("enclosures").child(enclosureName).child("reviews")
        reviewRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reviewList = mutableListOf<Pair<String, Review>>()
                for (reviewSnapshot in snapshot.children) {
                    val reviewId = reviewSnapshot.key ?: ""
                    val userId = reviewSnapshot.child("userId").getValue(String::class.java) ?: ""
                    val rating = reviewSnapshot.child("rating").getValue(Int::class.java) ?: 0
                    val comment = reviewSnapshot.child("comment").getValue(String::class.java) ?: ""
                    reviewList.add(Pair(reviewId, Review(userId, rating, comment)))
                }
                reviews = reviewList
                Log.d("Firebase", "Avis récupérés : ${reviews.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erreur de lecture", error.toException())
            }
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "🦁 Enclos : $enclosureName", style = MaterialTheme.typography.h5)

        // Champ pour entrer une note et un commentaire
        TextField(value = rating, onValueChange = { rating = it }, label = { Text("Note (1 à 5)") })
        TextField(value = comment, onValueChange = { comment = it }, label = { Text("Commentaire") })

        Button(onClick = {
            val userId = currentUser?.uid ?: return@Button
            val newReview = Review(userId, rating.toIntOrNull() ?: 0, comment)
            db.reference.child("enclosures").child(enclosureName).child("reviews").push()
                .setValue(newReview)
                .addOnSuccessListener {
                    Toast.makeText(context, "Avis ajouté", Toast.LENGTH_SHORT).show()
                }
        }) {
            Text("Soumettre l'avis")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Avis des visiteurs", style = MaterialTheme.typography.h6)
        LazyColumn {
            items(reviews) { reviewPair ->
                val reviewId = reviewPair.first
                val review = reviewPair.second

                Card(
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "⭐ Note : ${review.rating}/5")
                        Text(text = "💬 ${review.comment}")

                        // ✅ Bouton de suppression si l'utilisateur est l'auteur de l'avis
                        if (review.userId == currentUser?.uid) {
                            Button(
                                onClick = {
                                    db.reference.child("enclosures").child(enclosureName)
                                        .child("reviews").child(reviewId).removeValue()
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Avis supprimé", Toast.LENGTH_SHORT).show()
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
