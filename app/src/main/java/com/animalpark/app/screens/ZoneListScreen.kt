package com.animalpark.app.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.*
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
import com.animalpark.app.models.EnclosureZone
import com.google.firebase.database.*
import kotlin.Exception

@Composable
fun ZoneListScreen(navController: NavController, db: FirebaseDatabase, isAdmin: Boolean) {
    val context = LocalContext.current
    val activity = context as? android.app.Activity
    activity?.let {
        WindowCompat.setDecorFitsSystemWindows(it.window, false)
    }

    var zones by remember { mutableStateOf(listOf<EnclosureZone>()) }

    LaunchedEffect(Unit) {
        val ref = db.reference.child("zoo")

        ref.get().addOnSuccessListener { snapshot ->
            val zoneList = snapshot.children.mapNotNull { zoneSnapshot ->
                val zoneId = zoneSnapshot.key ?: return@mapNotNull null
                val zoneName = zoneSnapshot.child("name").getValue(String::class.java) ?: "Zone $zoneId"
                val enclosuresCount = zoneSnapshot.child("enclosures").childrenCount.toInt()

                val hexColor = zoneSnapshot.child("color").getValue(String::class.java) ?: "#CCCCCC"
                val parsedColor = try {
                    Color(android.graphics.Color.parseColor(hexColor))
                } catch (e: Exception) {
                    Log.e("ColorParse", "Erreur de couleur : $hexColor", e)
                    Color.Gray
                }

                EnclosureZone(zoneId, zoneName, enclosuresCount, parsedColor)
            }
            zones = zoneList.sortedBy { it.name }
            Log.d("Firebase", "Zones récupérées: ${zones.size}")
        }.addOnFailureListener {
            Log.e("Firebase", "Erreur de lecture", it)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(72.dp),
                title = {
                    Column(modifier = Modifier.padding(top = 30.dp)) {
                        Text("Zones du parc zoologique")
                    }
                }
            )
        },
        bottomBar = {
            Spacer(modifier = Modifier.height(70.dp))
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(WindowInsets.systemBars.asPaddingValues()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(5.dp))

            // 🎨 Nouveau Titre Stylisé
            Text(
                text = "🌿Découvrez les zones du parc🌿",
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 12.dp)
                    .background(Color(0xFFE0F7FA), shape = MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(zones.size) { index ->
                    val zone = zones[index]
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp) // 🔽 Hauteur réduite
                            .clickable {
                                navController.navigate("enclosureList/${zone.id}")
                            },
                        backgroundColor = zone.color,
                        elevation = 4.dp // 🌟 un peu plus d’ombre pour du relief
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🦁 ${zone.name.uppercase()}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B1B1B) // Couleur foncée pour le contraste
                            )
                            Text(
                                text = "Nombre d'enclos : ${zone.enclosuresCount}",
                                fontSize = 13.sp,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                color = Color(0xFF4F4F4F) // Gris doux
                            )
                        }
                    }
                }
            }

        }
    }
}
