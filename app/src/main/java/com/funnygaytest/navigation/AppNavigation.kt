package com.funnygaytest.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.funnygaytest.ui.screens.endings.EndingsScreen
import com.funnygaytest.ui.screens.game.GameScreen
import com.funnygaytest.ui.screens.result.ResultScreen
import com.funnygaytest.ui.screens.start.StartScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    onRequestShowAd: () -> Unit
) {
    NavHost(navController = navController, startDestination = ScreenRoutes.Start.route) {

        composable(ScreenRoutes.Start.route) {
            StartScreen(
                onGameStart = {
                    navController.navigate(ScreenRoutes.Game.route)
                },
                onEndingsShow = {
                    navController.navigate(ScreenRoutes.Endings.route)
                },
                onLoseResultShow = {
                    navController.navigate(ScreenRoutes.Result.withArgs("true")) {
                        popUpTo(ScreenRoutes.Start.route) { inclusive = true }
                    }
                }
            )
        }

        composable(ScreenRoutes.Endings.route) {
            EndingsScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(ScreenRoutes.Game.route) {
            GameScreen(
                goToResult = {
                    navController.navigate(ScreenRoutes.Result.withArgs("false")) {
                        popUpTo(ScreenRoutes.Start.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = ScreenRoutes.Result.route + "/{$LOSE_RESULT}",
            arguments = listOf(
                navArgument(LOSE_RESULT) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { navBackStackEntry ->

            val isPermanentLose = navBackStackEntry.arguments?.getBoolean(LOSE_RESULT) ?: false

            LaunchedEffect(Unit) {
                if (!isPermanentLose) onRequestShowAd()
            }

            ResultScreen(
                goToStart = {
                    navController.navigate(ScreenRoutes.Start.route) {
                        popUpTo(ScreenRoutes.Result.route + "/{$LOSE_RESULT}") {
                            inclusive = true
                        }
                    }
                }
            )
        }
    }
}