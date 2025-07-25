package com.example.crudjetpackcompose.Screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.crudjetpackcompose.Navigation.NavigationItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
@Preview
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Login",
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(fontSize = 32.sp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    label = { Text(text = "Enter Your Email") },
                    value = email.value,
                    onValueChange = {
                        email.value = it
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                TextField(
                    label = { Text(text = "Password") },
                    value = password.value,
                    onValueChange = {
                        password.value = it
                    }
                )

                Spacer(modifier = Modifier.height(15.dp))

                Row(modifier = Modifier.padding(12.dp)) {
                    // ➡️ Login Button
                    Button(
                        modifier = Modifier.padding(10.dp),
                        onClick = {
                            if (email.value.trim().isEmpty() || password.value.trim().isEmpty()) {
                                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                            } else {
                                loginUser(email.value.trim(), password.value.trim(), context, navController)
                            }
                        },
                        enabled = true,
                        shape = RectangleShape
                    ) {
                        Text(text = "Submit", style = TextStyle(fontSize = 16.sp))
                    }

                    // ➡️ SignUp Button (Fixed navigation logic)
                    Button(
                        modifier = Modifier.padding(10.dp),
                        onClick = {
                            navController.navigate(NavigationItems.SignUpScreen.route)
                        },
                        enabled = true,
                        shape = RectangleShape
                    ) {
                        Text(text = "SignUp", style = TextStyle(fontSize = 16.sp))
                    }
                }
            }
        }
    }
}

fun loginUser(email: String, password: String, context: android.content.Context, navController: NavController) {
    val auth = FirebaseAuth.getInstance()

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentUser = auth.currentUser
                val userId = currentUser?.uid

                if (userId != null) {

                    fetchUserName(userId) { name ->
                        if (name != null) {
                            Toast.makeText(context, "Welcome, $name", Toast.LENGTH_SHORT).show()


                            navController.navigate(NavigationItems.NoteScreen.route+"/$name") {
                                popUpTo(NavigationItems.LoginScreen.route) {
                                    inclusive = true
                                }
                            }
                        } else {
                            Toast.makeText(context, "User data not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}


fun fetchUserName(userId: String, onComplete: (String?) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("users").document(userId)

    userRef.get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val name = document.getString("name")
                onComplete(name)  // <-- Pass the name back to the caller
            } else {
                onComplete(null)  // User data not found
            }
        }
        .addOnFailureListener {
            onComplete(null) // Error occurred
        }
}

