package com.animalpark.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.animalpark.app.screens.ServiceListScreen
import com.animalpark.app.screens.ServiceDetailScreen
import com.google.firebase.database.FirebaseDatabase

@Composable
fun NavGraph(navController: NavHostController, db: FirebaseDatabase) {
    NavHost(navController = navController, startDestination = "serviceList") {
        composable("serviceList") {
            ServiceListScreen(navController, db)
        }
        composable("serviceDetail/{serviceName}/{location}") { backStackEntry ->
            val serviceName = backStackEntry.arguments?.getString("serviceName") ?: "Inconnu"
            val location = backStackEntry.arguments?.getString("location") ?: "Emplacement inconnu"
            ServiceDetailScreen(serviceName, location)
        }
    }
}
