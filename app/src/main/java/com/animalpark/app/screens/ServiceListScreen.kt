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
import com.animalpark.app.models.Service
import com.google.firebase.database.*

@Composable
fun ServiceListScreen(navController: NavController, db: FirebaseDatabase) {
    var services by remember { mutableStateOf(listOf<Service>()) }

    // 🔍 Récupération des services depuis Firebase
    LaunchedEffect(Unit) {
        val ref = db.reference.child("services")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val serviceList = mutableListOf<Service>()
                for (serviceSnapshot in snapshot.children) {
                    val id = serviceSnapshot.child("id").getValue(String::class.java) ?: "Inconnu"
                    val name = serviceSnapshot.child("name").getValue(String::class.java) ?: "Service inconnu"
                    val location = serviceSnapshot.child("location").getValue(String::class.java) ?: "Emplacement inconnu"

                    serviceList.add(Service(id, name, location))
                }
                services = serviceList
                Log.d("Firebase", "Nombre de services récupérés: ${services.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erreur de lecture", error.toException())
            }
        })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(72.dp),
                title = {
                    Column(modifier = Modifier.padding(top = 30.dp)) {
                        Text("Services du parc")
                    }
                }
            )
        },
        bottomBar = {
            Spacer(modifier = Modifier.height(70.dp)) // Ajoute un espace pour le footer de l'app
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .padding(innerPadding)
            .padding(16.dp)) {

            if (services.isEmpty()) {
                Text(text = "Aucun service trouvé.", modifier = Modifier.padding(16.dp))
            }

            LazyColumn {
                items(services) { service ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                navController.navigate("serviceDetail/${service.name}/${service.location}")
                            },
                        elevation = 4.dp
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "📍 ${service.name}", style = MaterialTheme.typography.h6)
                            Text(text = "📌 Emplacement : ${service.location}")
                        }
                    }
                }
            }
        }
    }
}


// 📌 Modèle de données pour un service

