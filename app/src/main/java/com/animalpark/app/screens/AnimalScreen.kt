package com.animalpark.app.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.database.*

data class Animal(val id: String, val name: String, val idEnclos: String)

@Composable
fun AnimalListScreen(
    navController: NavController,
    db: FirebaseDatabase,
    zoneId: String,
    enclosureId: String
) {
    var animals by remember { mutableStateOf(listOf<Animal>()) }
    var errorMessage by remember { mutableStateOf("") }
    var enclosureName by remember { mutableStateOf("Animaux") }
    var biomeColor by remember { mutableStateOf(Color(0xFF6200EE)) }

    // 🔄 Récupération Firebase
    LaunchedEffect(enclosureId) {
        val ref = db.reference.child("zoo").child(zoneId).child("enclosures").child(enclosureId)

        ref.child("name").get().addOnSuccessListener {
            enclosureName = it.getValue(String::class.java) ?: "Animaux"
        }

        db.reference.child("zoo").child(zoneId).child("color").get().addOnSuccessListener { snap ->
            snap.getValue(String::class.java)?.let { hex ->
                try {
                    biomeColor = Color(android.graphics.Color.parseColor(hex))
                } catch (_: Exception) {}
            }
        }

        ref.child("animals").get().addOnSuccessListener { snapshot ->
            animals = snapshot.children.mapNotNull { animalSnapshot ->
                val name = animalSnapshot.child("name").getValue(String::class.java)
                val id = animalSnapshot.child("id").getValue(String::class.java)
                val idEnclos = animalSnapshot.child("id_enclos").getValue(String::class.java)
                if (name != null && id != null && idEnclos != null)
                    Animal(id, name, idEnclos) else null
            }
            if (animals.isEmpty()) errorMessage = "Aucun animal trouvé."
        }.addOnFailureListener {
            errorMessage = "Erreur de lecture : ${it.message}"
            Log.e("Firebase", "Erreur Firebase", it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = biomeColor,
                modifier = Modifier.height(72.dp),
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 12.dp)
                    ) {
                        // 🔽 Déplacement du bouton retour
                        Box(modifier = Modifier.padding(top = 14.dp)) {
                            IconButton(onClick = {
                                val didPop = navController.popBackStack()
                                if (!didPop) {
                                    navController.navigate("enclosureList/$zoneId")
                                }
                            }) {
                                Icon(Icons.Filled.ArrowBack, contentDescription = "Retour", tint = Color.White)
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // 🔽 Déplacement du texte "ANIMAUX"
                        Column(modifier = Modifier.padding(top = 14.dp)) {
                            Text(
                                text = "🐾 ${enclosureName.uppercase()}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            )

        },
        // ✅ Active le bouton flottant ici si besoin
        /*
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate("addAnimal/$zoneId/$enclosureId")
            }) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un animal")
            }
        }
        */
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (errorMessage.isNotEmpty()) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(animals) { animal ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(90.dp),
                            elevation = 4.dp,
                            backgroundColor = Color(0xFFF8F8F8),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "🦁 ${animal.name}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ID : ${animal.id} | Enclos : ${animal.idEnclos}",
                                    fontSize = 14.sp,
                                    color = Color.DarkGray
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
