package com.animalpark.app.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase

@Composable
fun ReviewListScreen(enclosureId: String, db: FirebaseDatabase, navController: NavController) {
    var reviews by remember { mutableStateOf(listOf<Review>()) }

    LaunchedEffect(enclosureId) {
        val ref = db.reference.child("comment").child("enclosures").child(enclosureId).child("comments")
        ref.get().addOnSuccessListener { snapshot ->
            val reviewList = snapshot.children.mapNotNull { reviewSnapshot ->
                val userId = reviewSnapshot.child("userId").getValue(String::class.java) ?: return@mapNotNull null
                val comment = reviewSnapshot.child("comment").getValue(String::class.java) ?: ""
                val rating = reviewSnapshot.child("rating").getValue(Int::class.java) ?: 0

                Review(userId, comment, rating)
            }
            reviews = reviewList.sortedByDescending { it.rating }
        }.addOnFailureListener {
            Log.e("Firebase", "Erreur lors de la récupération des avis", it)
        }
    }

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
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.padding(top = 18.dp)) {
                            Text(
                                text = "🗂 Avis de l'enclos",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            if (reviews.isEmpty()) {
                Text("Aucun avis disponible.", style = MaterialTheme.typography.body1)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(reviews) { review ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            elevation = 4.dp
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "⭐ Note : ${review.rating}/5",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = review.comment)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "👤 Utilisateur : ${review.userId}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class Review(
    val userId: String,
    val comment: String,
    val rating: Int
)
