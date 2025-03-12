package com.animalpark.app.screens


import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.animalpark.app.models.User
import androidx.compose.ui.text.input.PasswordVisualTransformation


@Composable
fun RegisterScreen(navController: NavController, auth: FirebaseAuth, db: FirebaseDatabase) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(value = email, onValueChange = { email = it }, label = { Text("Email") })
        TextField(value = password, onValueChange = { password = it }, label = { Text("Mot de passe") }, visualTransformation = PasswordVisualTransformation())
        Button(onClick = {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = User(auth.currentUser!!.uid, email)
                        db.reference.child("users").child(user.uid).setValue(user)
                        Toast.makeText(context, "Inscription réussie", Toast.LENGTH_SHORT).show()
                        navController.navigate("login")
                    } else {
                        Toast.makeText(context, "Erreur: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }) {
            Text("S'inscrire")
        }
    }
}