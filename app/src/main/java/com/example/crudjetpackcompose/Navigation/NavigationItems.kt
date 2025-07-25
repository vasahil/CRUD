package com.example.crudjetpackcompose.Navigation

sealed class NavigationItems(val route:String){

    object SplashScreen : NavigationItems(route = "splash")
    object SignUpScreen : NavigationItems(route = "SignUp")
    object LoginScreen : NavigationItems(route = "login")
    object NoteScreen : NavigationItems(route = "home")
    object InsertNoteScreen : NavigationItems(route = "insert")
}