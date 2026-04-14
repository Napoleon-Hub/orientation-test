package com.funnygaytest.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.funnygaytest.ui.screens.endings.EndingsScreen
import com.funnygaytest.ui.screens.feed.FeedScreen
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
                    navController.navigate(ScreenRoutes.Result.route) {
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
                    navController.navigate(ScreenRoutes.Result.route) {
                        popUpTo(ScreenRoutes.Start.route) { inclusive = true }
                    }
                    onRequestShowAd()
                }
            )
        }

        composable(ScreenRoutes.Result.route) {
            ResultScreen(
                onFeedScreen = {
                    navController.navigate(ScreenRoutes.Feed.route)
                },
                onStartScreen = {
                    navController.navigate(ScreenRoutes.Start.route) {
                        popUpTo(ScreenRoutes.Result.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(ScreenRoutes.Feed.route) {
            FeedScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

    }
}