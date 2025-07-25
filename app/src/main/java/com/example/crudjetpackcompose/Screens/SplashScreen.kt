package com.example.crudjetpackcompose.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.crudjetpackcompose.Navigation.NavigationItems
import com.example.crudjetpackcompose.R
import com.example.crudjetpackcompose.ui.theme.colorBlack
import kotlinx.coroutines.delay

@Preview
@Composable
fun SplashScreen(navController: NavController){
    Scaffold { padding ->
        Box(
            modifier = Modifier.padding(padding)
                .background(color = colorBlack)
                .fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier.size(150.dp)
                    .align(alignment = Alignment.Center)
            )
        }
    }

        LaunchedEffect(Unit) {
            delay(1500)
            navController.navigate(NavigationItems.SignUpScreen.route){
                popUpTo(NavigationItems.SplashScreen.route){
                    inclusive =  true
                }
            }
        }



}