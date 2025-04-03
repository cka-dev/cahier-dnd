package com.example.cahier.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.cahier.ui.DrawingCanvas
import com.example.cahier.ui.HomeDestination
import com.example.cahier.ui.HomePane
import com.example.cahier.ui.NoteCanvas


@Composable
fun CahierNavHost(
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route
    ) {
        composable(HomeDestination.route) {

            HomePane(
                navigateToCanvas = { noteId ->
                    navController.navigate("${TextCanvasDestination.route}/$noteId")
                },
                navigateToDrawingCanvas = { noteId ->
                    navController.navigate("${DrawingCanvasDestination.route}/$noteId")
                },
                navigateUp = {
                    navController.navigateUp()
                },
            )
        }
        composable(
            route = TextCanvasDestination.routeWithArgs,
            arguments = listOf(navArgument(TextCanvasDestination.NOTE_ID_ARG) {
                type = NavType.LongType
            })
        ) { navBackStackEntry ->
            NoteCanvas(
                navBackStackEntry = navBackStackEntry,
                onExit = { navController.navigateUp() },
            )
        }
        composable(
            route = DrawingCanvasDestination.routeWithArgs,
            arguments = listOf(navArgument(DrawingCanvasDestination.NOTE_ID_ARG) {
                type = NavType.LongType
            })
        ) { navBackStackEntry ->
            DrawingCanvas(
                navBackStackEntry = navBackStackEntry,
                navigateUp = { navController.navigateUp() },
            )
        }
    }
}

object TextCanvasDestination : NavigationDestination {
    override val route = "note_canvas"
    const val NOTE_ID_ARG = "noteId"
    val routeWithArgs = "$route/{$NOTE_ID_ARG}"
}


object DrawingCanvasDestination : NavigationDestination {
    override val route = "drawing_canvas"
    const val NOTE_ID_ARG = "noteId"
    val routeWithArgs = "$route/{$NOTE_ID_ARG}"
}