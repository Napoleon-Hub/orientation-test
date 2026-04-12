package com.funnygaytest.navigation

sealed class ScreenRoutes(val route: String) {

    data object Start : ScreenRoutes("start")
    data object Endings : ScreenRoutes("endings")
    data object Game : ScreenRoutes("game")
    data object Result : ScreenRoutes("result")
    data object Feed : ScreenRoutes("feed")

    fun withArgs(vararg args: String?): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}