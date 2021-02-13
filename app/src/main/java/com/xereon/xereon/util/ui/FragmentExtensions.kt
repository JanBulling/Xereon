package com.xereon.xereon.util.ui

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.xereon.xereon.ui.doNavigate

fun Fragment.doNavigate(direction: NavDirections)= findNavController().doNavigate(direction)

fun Fragment.popBackStack(): Boolean {
    if (!isAdded) {
        IllegalStateException("Fragment is not added").also {
            Log.w("XEREON", it.message + "Trying to pop backstack on Fragment that isn't added to an Activity.")
        }
        return false
    }
    return findNavController().popBackStack()
}