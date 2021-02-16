package com.xereon.xereon.ui.main.search

sealed class SearchEvents {

    data class NavigateToStore(val storeId: Int, val storeName: String) : SearchEvents()

    object OpenSortMenu : SearchEvents()
}