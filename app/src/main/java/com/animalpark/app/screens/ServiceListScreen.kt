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

    LaunchedEffect(Unit) {
        val ref = db.reference.child("services")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val serviceList = mutableListOf<Service>()
                for (serviceSnapshot in snapshot.children) {
                    val service = serviceSnapshot.getValue(Service::class.java)
                    if (service != null) {
                        serviceList.add(service)
                    }
                }
                services = serviceList
                Log.d("Firebase", "Nombre de services récupérés: ${services.size}")
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Erreur de lecture", error.toException())
            }
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "🏛 Services du Parc", style = MaterialTheme.typography.h5)

        if (services.isEmpty()) {
            Text(text = "Aucun service disponible.", modifier = Modifier.padding(16.dp))
        }

        LazyColumn {
            items(services) { service ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable {
                            navController.navigate("serviceDetail/${service.name}/${service.latitude}/${service.longitude}")
                        },
                    elevation = 4.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "📌 ${service.name}", style = MaterialTheme.typography.h6)
                        Text(text = "🔹 Type : ${service.type}")
                    }
                }
            }
        }
    }
}
