package com.funnygaytest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.funnygaytest.screens.game.GameScreen
import com.funnygaytest.screens.result.ResultScreen
import com.funnygaytest.screens.start.StartScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    onRequestLoadAd: () -> Unit,
    onRequestShowAd: () -> Unit
) {
    NavHost(navController = navController, startDestination = ScreenRoutes.Start.route) {

        composable(ScreenRoutes.Start.route) {
            StartScreen(
                onGameStart = {
                    navController.navigate(ScreenRoutes.Game.route)
                },
                onLoseResultShow = {
                    navController.navigate(ScreenRoutes.Result.withArgs("true"))
                }
            )
        }

        composable(ScreenRoutes.Game.route) {
            GameScreen(
                goToResult = {
                    navController.navigate(ScreenRoutes.Result.withArgs("false"))
                }
            )
        }

        composable(
            route = ScreenRoutes.Result.route + "/${NavArgs.LOSE_RESULT}",
            arguments = listOf(
                navArgument(NavArgs.LOSE_RESULT) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { navBackStackEntry ->

            val isPermanentLose = navBackStackEntry.arguments?.getBoolean(NavArgs.LOSE_RESULT) ?: false

            LaunchedEffect(Unit) {
                if (!isPermanentLose) onRequestShowAd()
            }

            ResultScreen(
                navController = navController
            )
        }
    }
}