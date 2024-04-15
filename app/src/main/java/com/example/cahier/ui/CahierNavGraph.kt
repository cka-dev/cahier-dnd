package com.example.cahier.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class CahierNavGraph {
    HOME,
    CANVAS
}

@Composable
fun CahierNavHost(
    navController: NavHostController = rememberNavController(),
) {
    val startDestination: String = CahierNavGraph.HOME.name

        NavHost(
            navController = navController,
            startDestination = startDestination
        ) {
            composable(CahierNavGraph.HOME.name) {
                HomeScreen(onButtonClick = { navController.navigate(CahierNavGraph.CANVAS.name) })
            }
            composable(CahierNavGraph.CANVAS.name) {
                NoteCanvas()
            }
    }
}