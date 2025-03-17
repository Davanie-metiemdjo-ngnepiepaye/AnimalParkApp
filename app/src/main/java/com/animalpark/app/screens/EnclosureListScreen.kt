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
    var zones by remember { mutableStateOf(listOf<EnclosureZone>()) }

    LaunchedEffect(Unit) {
        val ref = db.reference.child("zoo")

        ref.get().addOnSuccessListener { snapshot ->
            val zoneList = snapshot.children.mapNotNull { zoneSnapshot ->
                val zoneId = zoneSnapshot.key ?: return@mapNotNull null
                val zoneName = zoneSnapshot.child("name").getValue(String::class.java) ?: "Zone $zoneId"
                val enclosureCount = zoneSnapshot.child("enclosures").childrenCount.toInt()

                EnclosureZone(zoneId, zoneName, enclosureCount)
            }

            zones = zoneList.sortedBy { it.name }
            Log.d("Firebase", "Zones récupérées: ${zones.size}")

        }.addOnFailureListener {
            Log.e("Firebase", "Erreur de lecture", it)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Liste des Zones", style = MaterialTheme.typography.h5)

        if (zones.isEmpty()) {
            Text(text = "Aucune zone trouvée.", modifier = Modifier.padding(16.dp))
        }

        LazyColumn {
            items(zones) { zone ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("enclosureZoneDetail/${zone.id}")
                        },
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "📍 ${zone.name}", style = MaterialTheme.typography.h6)
                        Text(text = "📦 Enclos : ${zone.enclosureCount}")
                    }
                }
            }
        }
    }
}

data class EnclosureZone(
    val id: String,
    val name: String,
    val enclosureCount: Int
)
