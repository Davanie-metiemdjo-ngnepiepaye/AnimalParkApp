package com.animalpark.app.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.*

@Composable
fun EnclosureListScreen(navController: NavController, db: FirebaseDatabase) {
    var enclosures by remember { mutableStateOf(listOf<Enclosure>()) }

    LaunchedEffect(Unit) {
        val ref = db.reference
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val enclosureList = mutableListOf<Enclosure>()
                for (zoneSnapshot in snapshot.children) { // 🔍 Chaque zone du zoo
                    val zoneName = zoneSnapshot.child("name").getValue(String::class.java) ?: ""

                    for (enclosureSnapshot in zoneSnapshot.child("enclosures").children) {
                        val id = enclosureSnapshot.child("id").getValue(String::class.java) ?: ""
                        val animals = enclosureSnapshot.child("animals").children.mapNotNull {
                            it.child("name").getValue(String::class.java)
                        }
                        if (id.isNotBlank()) {
                            enclosureList.add(Enclosure("$zoneName - Enclos $id", animals))
                        }
                    }
                }
                enclosures = enclosureList
                Log.d("Firebase", "Nombre d'enclos récupérés: ${enclosures.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erreur de lecture", error.toException())
            }
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Liste des Enclos", style = MaterialTheme.typography.h5)

        if (enclosures.isEmpty()) {
            Text(text = "Aucun enclos trouvé.", modifier = Modifier.padding(16.dp))
        }

        LazyColumn {
            items(enclosures) { enclosure ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("enclosureDetail/${enclosure.name}")
                        },
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "📍 ${enclosure.name}", style = MaterialTheme.typography.h6)
                        Text(text = "🐾 Animaux : ${enclosure.animals.joinToString(", ")}")
                    }
                }
            }
        }
    }
}

data class Enclosure(
    val name: String,
    val animals: List<String>
)