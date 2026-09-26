package com.funnygaytest.navigation

import androidx.navigation3.runtime.NavKey

fun MutableList<NavKey>.navigateTo(route: Route) {
    if (lastOrNull() != route) add(route)
}

fun MutableList<NavKey>.replaceAllWith(route: Route) {
    if (size == 1 && first() == route) return
    add(route)
    while (size > 1) removeAt(0)
}

fun MutableList<NavKey>.popBack() {
    if (size > 1) removeAt(lastIndex)
}
