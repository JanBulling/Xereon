package com.xereon.xereon.ui

interface MainActivityCallback {

    fun setActionBarTitle(title: String)
    fun setToolbarVisibility(visible: Boolean)
    fun setBottomNavBarVisibility(visible: Boolean)
}