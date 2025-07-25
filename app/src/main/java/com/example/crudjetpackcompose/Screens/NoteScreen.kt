package com.example.crudjetpackcompose.Screens

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.example.crudjetpackcompose.Models.Notes
import com.example.crudjetpackcompose.Navigation.NavigationItems
import com.example.crudjetpackcompose.ui.theme.colorBlack
import com.example.crudjetpackcompose.ui.theme.colorGray
import com.example.crudjetpackcompose.ui.theme.colorWhite
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NoteScreen(navController: NavController, userName: String?) {

    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser
    val noteList = remember { mutableStateListOf<Notes>() }
    val dataValue = remember { mutableStateOf(false) }


  //  println("Current User: ${currentUser?.email}, UID: ${currentUser?.uid}")

    LaunchedEffect(Unit) {
        currentUser?.uid?.let { uid ->
            val notesDbRef = db.collection("users")
                .document(uid)
                .collection("notes")

            notesDbRef.addSnapshotListener { value, error ->
                if (error == null) {
                    val data = value?.toObjects(Notes::class.java)
                    noteList.clear()
                    if (data != null) {
                        println("Data fetched: $data")
                        noteList.addAll(data)
                        dataValue.value = true
                    }
                } else {
                    println("Firestore Error: ${error.message}")
                    dataValue.value = false
                }
            }
        } ?: run {
            println("Error: No user is currently logged in.")
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                contentColor = Color.White,
                containerColor = Color.Red,
                shape = RoundedCornerShape(corner = CornerSize(100.dp)),
                onClick = {
                    navController.navigate(NavigationItems.InsertNoteScreen.route + "/defaultId")
                }
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .background(color = colorBlack)
                .fillMaxSize()
        ) {

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Hello $userName",
                    color = colorWhite,
                    style = TextStyle(fontSize = 32.sp),
                    fontWeight = FontWeight.Bold

                )

                LazyColumn {
                    items(noteList) { note ->
                        // Pass notesDbRef here!
                        ListItem(note, db.collection("users").document(currentUser!!.uid).collection("notes"), navController)
                    }
                }
            }
        }
    }
}

@Composable
fun ListItem(note: Notes, notesDbRef: CollectionReference, navController: NavController) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(shape = RoundedCornerShape(corner = CornerSize(20.dp)))
            .background(color = colorGray)
    ) {

        DropdownMenu(
            modifier = Modifier
                .background(color = Color.White)
                .clip(RoundedCornerShape(corner = CornerSize(20.dp)))
                .height(100.dp),
            properties = PopupProperties(clippingEnabled = true),
            offset = androidx.compose.ui.unit.DpOffset(x = (-30).dp, y = (-90).dp),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = "Update", style = TextStyle(color = colorGray)) },
                onClick = {
                    navController.navigate(NavigationItems.InsertNoteScreen.route + "/${note.id}")
                    expanded = false
                }
            )
            HorizontalDivider(thickness = 1.dp, color = colorGray)
            DropdownMenuItem(
                text = { Text(text = "Delete", style = TextStyle(color = colorGray)) },
                onClick = {
                    val alertDialog = AlertDialog.Builder(context)
                    alertDialog.setMessage("Are you sure you want to delete?")
                    alertDialog.setPositiveButton("Yes") { dialog, _ ->
                        notesDbRef.document(note.id).delete()
                        dialog.dismiss()
                        expanded = false
                    }
                    alertDialog.setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    alertDialog.show()
                }
            )
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "",
            tint = Color.White,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(10.dp)
                .clickable { expanded = true }
        )

        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = note.title, style = TextStyle(color = colorWhite, fontSize = 25.sp))
            Text(text = note.description, style = TextStyle(color = Color.LightGray, fontSize = 12.sp))
        }
    }
}