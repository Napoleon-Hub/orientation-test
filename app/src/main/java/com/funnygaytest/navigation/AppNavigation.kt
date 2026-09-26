package com.funnygaytest.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.funnygaytest.ui.screens.endings.EndingsScreen
import com.funnygaytest.ui.screens.feed.FeedScreen
import com.funnygaytest.ui.screens.game.GameScreen
import com.funnygaytest.ui.screens.result.ResultScreen
import com.funnygaytest.ui.screens.start.StartScreen

private const val TransitionDurationMs = 700

@Composable
fun AppNavigation(onRequestShowAd: () -> Unit) {
    val backStack = rememberNavBackStack(Route.Start)
    val fade = { fadeIn(tween(TransitionDurationMs)) togetherWith fadeOut(tween(TransitionDurationMs)) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.popBack() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = { fade() },
        popTransitionSpec = { fade() },
        predictivePopTransitionSpec = { fade() },
        entryProvider = entryProvider {
            entry<Route.Start> {
                StartScreen(
                    onGameStart = { backStack.navigateTo(Route.Game) },
                    onEndingsShow = { backStack.navigateTo(Route.Endings) },
                    onLoseResultShow = { backStack.replaceAllWith(Route.Result) }
                )
            }

            entry<Route.Endings> {
                EndingsScreen(onBack = { backStack.popBack() })
            }

            entry<Route.Game> {
                GameScreen(
                    goToResult = {
                        backStack.replaceAllWith(Route.Result)
                        onRequestShowAd()
                    }
                )
            }

            entry<Route.Result> {
                ResultScreen(
                    onFeedScreen = { backStack.navigateTo(Route.Feed) },
                    onStartScreen = { backStack.replaceAllWith(Route.Start) }
                )
            }

            entry<Route.Feed> {
                FeedScreen(onBack = { backStack.popBack() })
            }
        }
    )
}
