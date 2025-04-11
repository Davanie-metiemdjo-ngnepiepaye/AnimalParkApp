package com.animalpark.app.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.animalpark.app.R
import com.google.firebase.database.FirebaseDatabase

@Composable
fun MapScreen(
    navController: NavController,
    db: FirebaseDatabase
) {
    var allPlaces by remember { mutableStateOf(listOf<String>()) }
    var startPoint by remember { mutableStateOf("") }
    var endPoint by remember { mutableStateOf("") }
    var routeResult by remember { mutableStateOf("") }

    // Charger les lieux disponibles
    LaunchedEffect(Unit) {
        db.reference.child("zoo").get().addOnSuccessListener { snapshot ->
            val list = mutableListOf<String>()
            snapshot.children.forEach { zone ->
                val zoneName = zone.child("name").getValue(String::class.java)
                zone.child("enclosures").children.forEach { enclosure ->
                    val encName = enclosure.child("name").getValue(String::class.java)
                    if (encName != null) list.add(encName)
                }
            }
            db.reference.child("services").get().addOnSuccessListener { serviceSnap ->
                serviceSnap.children.forEach {
                    it.child("name").getValue(String::class.java)?.let { name -> list.add(name) }
                }
                allPlaces = list.sorted()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(72.dp),
                title = {
                    Column(modifier = Modifier.padding(top = 30.dp)) {
                        Text("Navigation dans le parc")
                    }
                }
            )
        },
        bottomBar = {
            Spacer(modifier = Modifier.height(70.dp)) // Ajoute un espace pour le footer de l'app
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Image(
                painter = painterResource(id = R.drawable.parc),
                contentDescription = "Carte du parc",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Text("Point de départ")
            DropdownMenuField(options = allPlaces, selected = startPoint, onSelected = { startPoint = it })

            Text("Destination")
            DropdownMenuField(options = allPlaces, selected = endPoint, onSelected = { endPoint = it })

            Button(onClick = {
                routeResult = when {
                    startPoint == endPoint -> "Vous êtes déjà à l'endroit souhaité."
                    startPoint.isBlank() || endPoint.isBlank() -> "Veuillez sélectionner un point de départ et une destination."
                    else -> "Itinéraire de \"$startPoint\" vers \"$endPoint\" :\n\n1. Marchez jusqu'au centre du parc.\n2. Suivez les panneaux directionnels vers \"$endPoint\".\n3. Arrivée prévue dans quelques minutes !"
                }
            }) {
                Text("Afficher l'itinéraire")
            }

            if (routeResult.isNotBlank()) {
                Text(routeResult, style = MaterialTheme.typography.body1)
            }
        }
    }
}

@Composable
fun DropdownMenuField(options: List<String>, selected: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Choisir") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(onClick = {
                    onSelected(option)
                    expanded = false
                }) {
                    Text(option)
                }
            }
        }
    }
}
