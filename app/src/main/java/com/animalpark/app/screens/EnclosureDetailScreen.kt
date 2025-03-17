package com.animalpark.app.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
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
fun EnclosureDetailScreen(navController: NavController, db: FirebaseDatabase, zoneId: String) {
    var animals by remember { mutableStateOf(listOf<String>()) }
    var zoneName by remember { mutableStateOf("Chargement...") }

    LaunchedEffect(zoneId) {
        val ref = db.reference.child("zoo").child(zoneId)

        ref.get().addOnSuccessListener { snapshot ->
            zoneName = snapshot.child("name").getValue(String::class.java) ?: "Zone inconnue"

            val animalList = snapshot.child("enclosures").children.flatMap { enclosureSnapshot ->
                enclosureSnapshot.child("animals").children.mapNotNull {
                    it.child("name").getValue(String::class.java)
                }
            }

            animals = animalList.distinct().sorted()
            Log.d("Firebase", "Animaux récupérés: ${animals.size}")

        }.addOnFailureListener {
            Log.e("Firebase", "Erreur de lecture", it)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Animaux de $zoneName", style = MaterialTheme.typography.h5)

        if (animals.isEmpty()) {
            Text(text = "Aucun animal trouvé.", modifier = Modifier.padding(16.dp))
        }

        LazyColumn {
            items(animals) { animal ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    elevation = 4.dp
                ) {
                    Text(
                        text = "🐾 $animal",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}
