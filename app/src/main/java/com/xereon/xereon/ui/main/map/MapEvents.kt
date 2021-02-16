package com.xereon.xereon.ui.main.map

sealed class MapEvents {

    data class NavigateToStore(val storeId: Int, val storeName: String) : MapEvents()
    object LoadStoresInRegion : MapEvents()
}