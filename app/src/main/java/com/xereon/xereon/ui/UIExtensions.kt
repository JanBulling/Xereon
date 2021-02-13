package com.xereon.xereon.ui

import androidx.navigation.NavController
import androidx.navigation.NavDirections

fun NavController.doNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)
        ?.let { navigate(direction) }
}