package com.funnygaytest.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface Route : NavKey {

    @Serializable
    data object Start : Route

    @Serializable
    data object Endings : Route

    @Serializable
    data object Game : Route

    @Serializable
    data object Result : Route

    @Serializable
    data object Feed : Route
}
