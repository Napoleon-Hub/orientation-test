package com.funnygaytest.navigation

sealed class ScreenRoutes(val route: String) {

    data object Start : ScreenRoutes("start")
    data object Game : ScreenRoutes("game")
    data object Result : ScreenRoutes("result")

    fun withArgs(vararg args: String?): String {
        return buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
    }
}