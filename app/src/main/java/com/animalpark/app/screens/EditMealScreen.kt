package com.animalpark.app.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.database.FirebaseDatabase

@Composable
fun EditMealScreen(
    navController: NavController,
    db: FirebaseDatabase,
    zoneId: String,
    enclosureId: String
) {
    val context = LocalContext.current
    var meal by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // ✅ Charger la valeur actuelle
    LaunchedEffect(Unit) {
        val ref = db.reference
            .child("zoo").child(zoneId)
            .child("enclosures").child(enclosureId)
            .child("meal")

        ref.get().addOnSuccessListener { snapshot ->
            meal = snapshot.getValue(String::class.java) ?: ""
            isLoading = false
        }.addOnFailureListener {
            Toast.makeText(context, "Erreur de lecture", Toast.LENGTH_SHORT).show()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Modifier l'horaire") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                OutlinedTextField(
                    value = meal,
                    onValueChange = { meal = it },
                    label = { Text("Horaire d'alimentation") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        val ref = db.reference
                            .child("zoo").child(zoneId)
                            .child("enclosures").child(enclosureId)
                            .child("meal")

                        ref.setValue(meal)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Horaire mis à jour", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Erreur de mise à jour", Toast.LENGTH_SHORT).show()
                            }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Enregistrer")
                }
            }
        }
    }
}
