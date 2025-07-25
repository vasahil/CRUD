package com.example.crudjetpackcompose.Screens


import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.crudjetpackcompose.Models.Notes
import com.example.crudjetpackcompose.Navigation.NavigationItems
import com.example.crudjetpackcompose.ui.theme.colorBlack
import com.example.crudjetpackcompose.ui.theme.colorGray
import com.example.crudjetpackcompose.ui.theme.colorRed
import com.example.crudjetpackcompose.ui.theme.colorWhite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable

fun InsertNoteScreen(navController: NavController, id1: String?){

    val context = LocalContext.current

    val db = FirebaseFirestore.getInstance()

    val auth = FirebaseAuth.getInstance()



    val currentUser = auth.currentUser
    val userId = currentUser?.uid

    if (userId == null) {
        Toast.makeText(context, "User not authenticated!", Toast.LENGTH_SHORT).show()
        return
    }

    val noteRef = db.collection("users").document(userId).collection("notes")


    val title = remember { mutableStateOf("") }
    val description = remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        if (!id1.isNullOrEmpty() && id1 != "defaultId") {
            // Logging the ID to check if it's correct
            println("Fetching document with ID: $id1")

            noteRef.document(id1).get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        val singleData = documentSnapshot.toObject(Notes::class.java)
                        println("Document Found: $singleData")

                        if (singleData != null) {
                            // Update the state safely
                            title.value = singleData.title ?: ""
                            description.value = singleData.description ?: ""
                        }
                    } else {
                        // Document not found
                        println("Document with ID: $id1 not found in Firestore.")
                        Toast.makeText(context, "Document not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    println("Error fetching document: ${exception.message}")
                    Toast.makeText(context, "Failed to fetch data: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            println("Invalid ID: $id1")
        }
    }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(onClick = {
                if(title.value.isEmpty() && description.value.isEmpty()){
                    Toast.makeText(context, "Enter valid data", Toast.LENGTH_SHORT).show()
                }else{
                    var myNotesId = ""
                    myNotesId = if(id1 != "defaultId" && id1 != null){
                        id1.toString()

                    }else{


                        noteRef.document().id


                    }
                    val notes = Notes(
                        id = myNotesId.toString(),
                        title = title.value,
                        description = description.value

                    )



                    noteRef.document(myNotesId).set(notes).addOnCompleteListener {
                        if(it.isSuccessful){
                            Toast.makeText(context, "Notes Inserted Successfully", Toast.LENGTH_SHORT).show()
                            navController.navigate(NavigationItems.NoteScreen.route)
                        }else{
                            Toast.makeText(context, "Something Went Wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }, contentColor = colorWhite, containerColor = colorRed, shape = RoundedCornerShape(corner = CornerSize(100.dp))
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "")
            }
        }
    ){ padding->
        Box(modifier = Modifier.padding(padding)
            .fillMaxSize()
            .background(color = colorBlack)){

            Column (modifier = Modifier.padding(15.dp)){
                Text(text = "Insert Data", style = TextStyle(color = colorWhite, fontSize = 32.sp),
                    fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(15.dp))

                TextField(textStyle = TextStyle(color = Color.White, fontSize = 12.sp)
                    ,colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorGray,
                    unfocusedContainerColor = colorGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent

                ), shape = RoundedCornerShape(corner = CornerSize(10.dp)),
                    label = {
                    Text(text = "Enter Your Title", style = TextStyle(fontSize = 17.sp, color = Color.Cyan))
                }, value = title.value, onValueChange = {
                    title.value = it

                }, modifier = Modifier.fillMaxWidth())


                Spacer(modifier = Modifier.height(18.dp))


                TextField(textStyle = TextStyle(color = Color.White, fontSize = 20.sp)
                    ,colors = TextFieldDefaults.colors(
                    focusedContainerColor = colorGray,
                    unfocusedContainerColor = colorGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent

                ), shape = RoundedCornerShape(corner = CornerSize(10.dp)),

                    label = {
                        Text(text = "Enter Your Description", style = TextStyle(fontSize = 17.sp, color = Color.Cyan), modifier = Modifier.padding(bottom = 8.dp))
                    }, value = description.value, onValueChange = {
                        description.value = it

                    }, modifier = Modifier.fillMaxWidth()
                        .fillMaxHeight(0.6f))


            }
        }
    }
}