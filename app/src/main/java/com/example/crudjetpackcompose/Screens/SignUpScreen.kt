package com.example.crudjetpackcompose.Screens

import android.content.Context
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
fun SignupScreen(navController: NavController) {
    val context = LocalContext.current

    val name = remember { mutableStateOf("") }
    val st = remember { mutableStateOf("") }
    val email = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "SignUp",
                    fontWeight = FontWeight.Bold,
                    style = TextStyle(fontSize = 32.sp)
                )

                Spacer(modifier = Modifier.height(15.dp))

                TextField(
                    label = { Text(text = "Name") },
                    value = name.value,
                    onValueChange = {
                        name.value = it
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))
                TextField(
                    label = { Text(text = "Student Or Professional") },
                    value = st.value,
                    onValueChange = {
                        st.value = it
                    }
                )
                Spacer(modifier = Modifier.height(10.dp))

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

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                        if(email.value.isEmpty() && password.value.isEmpty()){
                            Toast.makeText(context,"Please fill all fields", Toast.LENGTH_SHORT).show()
                        }else{
                            signUpUser(email.value, password.value, name.value, st.value, context) {
                                navController.navigate(NavigationItems.InsertNoteScreen.route+"/defaultId")
                            }
                        }

                    }
                ) {
                    Text(text = "Submit", style = TextStyle(fontSize = 16.sp))
                }

                Button(
                    modifier = Modifier.padding(10.dp),
                    onClick = {
                        navController.navigate(NavigationItems.LoginScreen.route)
                    }
                ) {
                    Text(text = "Login", style = TextStyle(fontSize = 16.sp))
                }
            }
        }
    }
}

fun signUpUser(
    email: String,
    password: String,
    name: String,
    status: String,
    context: Context,
    onSuccess: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    Toast.makeText(context, "SignUp successful: User ID: $userId", Toast.LENGTH_SHORT).show()
                    val userData = hashMapOf(
                        "email" to email,
                        "userId" to userId,
                        "name" to name,
                        "status" to status,
                        "createdAt" to System.currentTimeMillis()
                    )


                    db.collection("users").document(userId)
                        .set(userData)
                        .addOnSuccessListener {
                            Toast.makeText(context, "User data saved in Firestore!", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Failed to save data: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "User ID is null", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "SignUp failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
            }
        }
}
