package com.alpha.selfemployment.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf


enum class NavDirection {
    Forward, Back
}

class AppNavigator {

    val backStack = mutableStateListOf<Screen>()
    var navDirection by mutableStateOf(NavDirection.Forward)
        private set

    fun navigate(screen: Screen) {
        navDirection = NavDirection.Forward
        backStack.add(screen)
    }

    fun pop(): Boolean {
        return if (backStack.size > 1) {
            navDirection = NavDirection.Back
            backStack.removeAt(backStack.lastIndex)
            true
        } else {
            false
        }
    }

    fun clearAndNavigate(screen: Screen) {
        navDirection = NavDirection.Forward
        backStack.clear()
        backStack.add(screen)
    }


    val currentScreen: Screen
        get() = backStack.last()

    val isStackHasOne : Boolean =if( backStack.size == 1)
    {
        println("backstach size if-- ${backStack.size}")
        true
    }
    else{
        println("backstach size else -- ${backStack.size}")
        false
    }

}

val LocalNavigator = staticCompositionLocalOf<AppNavigator> {
    error("Navigator not provided")
}