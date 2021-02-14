package com.xereon.xereon.util.ui

import android.os.Build
import android.util.Log
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
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

fun Fragment.showError(@StringRes message: Int) {
    val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        snackBar.view.setBackgroundColor(resources.getColor(R.color.error, null))
    snackBar.show()
}