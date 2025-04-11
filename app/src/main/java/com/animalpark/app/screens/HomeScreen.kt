package com.animalpark.app.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.animalpark.app.R
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    navController: NavController,
    auth: FirebaseAuth,
    isAdmin: Boolean = false
) {
    Scaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.height(80.dp), // un peu plus haute
                backgroundColor = MaterialTheme.colors.primary,
                //contentPadding = PaddingValues(horizontal = 16.dp),
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp), // 💡 décalage vers le bas
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = " Acceuil ",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        IconButton(onClick = {
                            auth.signOut()
                            navController.navigate("login") {
                                popUpTo(0) { inclusive = true }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Déconnexion",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        }

    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 🎨 Image de fond
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Image de fond",
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // ✅ Texte de bienvenue
                Box(
                    modifier = Modifier
                        .background(Color.Blue.copy(alpha = 0.7f), shape = RoundedCornerShape(12.dp))
                        .padding(35.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Bienvenue dans ZooApp",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        if (isAdmin) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "👑 Vous êtes connecté en tant qu'ADMIN",
                                color = Color.Yellow,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ✅ Image illustratrice (ou logo)
                Image(
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = "Zoo Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    alignment = Alignment.Center
                )

                Spacer(modifier = Modifier.height(30.dp))

                // ✅ Bouton principal
                Button(
                    onClick = { navController.navigate("enclosures") },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(30.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF6200EE))
                ) {
                    Text("Explorer le Zoo", fontSize = 22.sp, color = Color.White)
                }
            }
        }
    }
}
