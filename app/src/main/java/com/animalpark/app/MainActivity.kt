package com.animalpark.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.animalpark.app.screens.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val auth = FirebaseAuth.getInstance()
        val db = FirebaseDatabase.getInstance()
        auth.signOut()

        setContent {
            val navController = rememberNavController()
            var isLoggedIn by remember { mutableStateOf(auth.currentUser != null) }
            var isAdmin by remember { mutableStateOf(false) }
            val scaffoldState = rememberScaffoldState()
            var adminMessageToShow by remember { mutableStateOf<String?>(null) }

            // 🔁 Auth listener
            LaunchedEffect(auth) {
                auth.addAuthStateListener { firebaseAuth ->
                    isLoggedIn = firebaseAuth.currentUser != null

                    val uid = firebaseAuth.currentUser?.uid
                    if (uid != null) {
                        db.reference.child("users").child(uid).child("isAdmin")
                            .get()
                            .addOnSuccessListener { snapshot ->
                                isAdmin = snapshot.getValue(Boolean::class.java) == true
                                adminMessageToShow = if (isAdmin)
                                    "👑 Connecté en tant qu'ADMIN"
                                else
                                    "👤 Connecté en tant que visiteur"

                                // ✅ Ajout automatique des champs maintenance manquants
                                ajouterMaintenanceSiManquant(db)
                            }
                    }
                }
            }

            // ✅ Snackbar pour retour rôle
            LaunchedEffect(adminMessageToShow) {
                adminMessageToShow?.let {
                    scaffoldState.snackbarHostState.showSnackbar(it)
                    adminMessageToShow = null
                }
            }

            Scaffold(
                scaffoldState = scaffoldState,
                bottomBar = { if (isLoggedIn) BottomNavigationBar(navController) },
                modifier = Modifier.padding(bottom = 15.dp)
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = if (isLoggedIn) "home" else "login",
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable("login") { LoginScreen(navController, auth) }
                    composable("register") { RegisterScreen(navController, auth, db) }
                    composable("home") { HomeScreen(navController, auth, isAdmin) }
                    composable("enclosures") { ZoneListScreen(navController, db, isAdmin) }
                    composable("enclosureList/{zoneId}") { backStackEntry ->
                        val zoneId = backStackEntry.arguments?.getString("zoneId") ?: return@composable
                        EnclosureListScreen(navController, db, zoneId, isAdmin)
                    }
                    composable("animalList/{zoneId}/{enclosureId}") { backStackEntry ->
                        val zoneId = backStackEntry.arguments?.getString("zoneId") ?: return@composable
                        val enclosureId = backStackEntry.arguments?.getString("enclosureId") ?: return@composable
                        AnimalListScreen(navController, db, zoneId, enclosureId)
                    }
                    composable("enclosureZoneDetail/{zoneId}/{enclosureId}") { backStackEntry ->
                        val zoneId = backStackEntry.arguments?.getString("zoneId") ?: return@composable
                        val enclosureId = backStackEntry.arguments?.getString("enclosureId") ?: return@composable
                        AnimalListScreen(navController, db, zoneId, enclosureId)
                    }
                    composable("services") { ServiceListScreen(navController, db) }
                    composable("serviceDetail/{serviceName}/{location}") { backStackEntry ->
                        val serviceName = backStackEntry.arguments?.getString("serviceName") ?: "Service inconnu"
                        val location = backStackEntry.arguments?.getString("location") ?: "Emplacement inconnu"
                        ServiceDetailScreen(serviceName, location)
                    }
                    composable("editMeal/{zoneId}/{enclosureId}") { backStackEntry ->
                        val zoneId = backStackEntry.arguments?.getString("zoneId") ?: return@composable
                        val enclosureId = backStackEntry.arguments?.getString("enclosureId") ?: return@composable
                        EditMealScreen(navController, FirebaseDatabase.getInstance(), zoneId, enclosureId)
                    }
                    composable("map") { MapScreen(navController, db) }
                    composable("review/{enclosureId}") { backStackEntry ->
                        val enclosureId = backStackEntry.arguments?.getString("enclosureId") ?: ""
                        AddReviewScreen(
                            enclosureId = enclosureId,
                            db = db,
                            auth = auth,
                            navController = navController // ✅ on passe le bon contrôleur ici
                        )
                    }
                    composable("reviewList/{enclosureId}") { backStackEntry ->
                        val enclosureId = backStackEntry.arguments?.getString("enclosureId") ?: return@composable
                        ReviewListScreen(enclosureId, db, navController)
                    }
                }
            }
        }
    }

    // ✅ Ajoute automatiquement maintenance: false si absent
    private fun ajouterMaintenanceSiManquant(db: FirebaseDatabase) {
        val zooRef = db.reference.child("zoo")
        zooRef.get().addOnSuccessListener { snapshot ->
            for (zoneSnapshot in snapshot.children) {
                val zoneId = zoneSnapshot.key ?: continue
                val enclosuresSnapshot = zoneSnapshot.child("enclosures")
                for (enclosureSnapshot in enclosuresSnapshot.children) {
                    val enclosureId = enclosureSnapshot.key ?: continue
                    val maintenance = enclosureSnapshot.child("maintenance").getValue(Boolean::class.java)
                    if (maintenance == null) {
                        db.reference.child("zoo")
                            .child(zoneId)
                            .child("enclosures")
                            .child(enclosureId)
                            .child("maintenance")
                            .setValue(false)
                        Log.d("MaintenanceFix", "Ajout de maintenance=false à zone $zoneId, enclos $enclosureId")
                    }
                }
            }
        }.addOnFailureListener {
            Log.e("MaintenanceFix", "Erreur Firebase : ${it.message}")
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "home"

    BottomNavigation(backgroundColor = MaterialTheme.colors.primary) {
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Accueil") },
            label = { Text("Accueil") },
            selected = currentRoute == "home",
            onClick = { navigateTo(navController, "home") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Menu, contentDescription = "Enclos") },
            label = { Text("ZOO") },
            selected = currentRoute == "enclosures",
            onClick = { navigateTo(navController, "enclosures") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.LocationOn, contentDescription = "Services") },
            label = { Text("Services") },
            selected = currentRoute == "services",
            onClick = { navigateTo(navController, "services") }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Star, contentDescription = "Plan") },
            label = { Text("Plan") },
            selected = currentRoute == "map",
            onClick = { navigateTo(navController, "map") }
        )

    }
}

fun navigateTo(navController: NavHostController, route: String) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    if (currentRoute != route) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}
