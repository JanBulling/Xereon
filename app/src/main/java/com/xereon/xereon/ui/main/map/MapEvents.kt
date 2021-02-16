package com.xereon.xereon.ui.main.map

import com.xereon.xereon.ui.map.MapViewModel

sealed class MapEvents {

    data class NavigateToStore(val storeId: Int, val storeName: String) : MapEvents()
    object LoadStoresInRegion : MapEvents()
}