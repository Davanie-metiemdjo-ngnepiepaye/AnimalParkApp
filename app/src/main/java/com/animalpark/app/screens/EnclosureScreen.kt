package com.animalpark.app.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import com.animalpark.app.models.Enclosure
import com.google.firebase.database.*

@Composable
fun EnclosureListScreen(
    navController: NavController,
    db: FirebaseDatabase,
    zoneId: String,
    isAdmin: Boolean
) {
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    activity?.let {
        WindowCompat.setDecorFitsSystemWindows(it.window, false)
    }

    var enclosures by remember { mutableStateOf(listOf<Enclosure>()) }
    var biomeColor by remember { mutableStateOf(Color.Gray) }
    var zoneName by remember { mutableStateOf("Zone") }

    LaunchedEffect(zoneId) {
        val zoneRef = db.reference.child("zoo").child(zoneId)

        zoneRef.child("name").get().addOnSuccessListener { snapshot ->
            zoneName = snapshot.getValue(String::class.java) ?: "Zone"
        }

        zoneRef.child("color").get().addOnSuccessListener { colorSnapshot ->
            val colorHex = colorSnapshot.getValue(String::class.java) ?: "#CCCCCC"
            try {
                biomeColor = Color(android.graphics.Color.parseColor(colorHex))
            } catch (_: Exception) {}
        }

        zoneRef.child("enclosures").get().addOnSuccessListener { snapshot ->
            val enclosureList = snapshot.children.mapNotNull { enclosureSnapshot ->
                val enclosureId = enclosureSnapshot.key ?: return@mapNotNull null
                val enclosureName = enclosureSnapshot.child("name").getValue(String::class.java) ?: "Enclos $enclosureId"
                val animalsCount = enclosureSnapshot.child("animals").childrenCount.toInt()
                val maintenance = enclosureSnapshot.child("maintenance").getValue(Boolean::class.java) ?: false

                Enclosure(enclosureId, enclosureName, animalsCount, maintenance)
            }
            enclosures = enclosureList
        }.addOnFailureListener {
            Log.e("Firebase", "Erreur de lecture", it)
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Retour")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.padding(top = 18.dp)) {
                            Text(
                                text = zoneName,
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
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(enclosures.size) { index ->
                    val enclosure = enclosures[index]

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(270.dp),
                        backgroundColor = Color(0xFFF8F8F8),
                        elevation = 3.dp,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "🦁 ${enclosure.name.uppercase()}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B1B1B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Animaux : ${enclosure.animalsCount}",
                                    fontSize = 13.sp,
                                    color = Color(0xFF444444)
                                )

                                if (enclosure.maintenance) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "🚧 En maintenance",
                                        fontSize = 12.sp,
                                        color = Color.Red,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Column(
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                if (!enclosure.maintenance || isAdmin) {
                                    Button(
                                        onClick = {
                                            navController.navigate("animalList/${zoneId}/${enclosure.id}")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(38.dp),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text("Voir", fontSize = 14.sp)
                                    }
                                }

                                if (isAdmin) {
                                    Button(
                                        onClick = {
                                            val ref = db.reference
                                                .child("zoo").child(zoneId)
                                                .child("enclosures").child(enclosure.id)
                                                .child("maintenance")
                                            ref.setValue(!enclosure.maintenance)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(38.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = if (enclosure.maintenance) Color(0xFF43A047) else Color(0xFFEF6C00)
                                        ),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text(
                                            text = if (enclosure.maintenance) "Réouvrir" else "Maintenance",
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            navController.navigate("editMeal/${zoneId}/${enclosure.id}")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(38.dp),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text("🕒 Horaire", fontSize = 14.sp)
                                    }

                                    Button(
                                        onClick = {
                                            navController.navigate("reviewList/${enclosure.id}")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(38.dp),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text("👁 Voir les avis", fontSize = 14.sp)
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            navController.navigate("review/${enclosure.id}")
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(38.dp),
                                        shape = MaterialTheme.shapes.small
                                    ) {
                                        Text("📝 Avis", fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
