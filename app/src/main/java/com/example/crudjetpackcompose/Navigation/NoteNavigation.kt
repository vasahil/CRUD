package com.example.crudjetpackcompose.Navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.crudjetpackcompose.Screens.InsertNoteScreen
import com.example.crudjetpackcompose.Screens.LoginScreen
import com.example.crudjetpackcompose.Screens.NoteScreen
import com.example.crudjetpackcompose.Screens.SignupScreen
import com.example.crudjetpackcompose.Screens.SplashScreen

@Composable
fun NoteNavigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
       // composable (route = "splash"){
        composable (NavigationItems.SplashScreen.route){
            SplashScreen(navController)


        }

        composable(NavigationItems.SignUpScreen.route) {
            SignupScreen(navController)
        }

        composable (NavigationItems.LoginScreen.route){
            LoginScreen(navController)
        }

        composable (NavigationItems.NoteScreen.route+"/{userName}"){backStackEntry->
            val userName = backStackEntry.arguments?.getString("userName")
            NoteScreen(navController, userName = userName)
        }

        composable(NavigationItems.InsertNoteScreen.route+"/{id}"){
            val id = it.arguments?.getString("id")
            InsertNoteScreen(navController, id)
        }
    }
}