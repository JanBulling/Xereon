package com.xereon.xereon.ui.stores

import com.google.android.gms.maps.model.LatLng
import com.xereon.xereon.data.store.StoreIdentificationData

sealed class StoreEvents {

    object NavigateBack : StoreEvents()
    object StoreReported : StoreEvents()
    object AddedToFavorites : StoreEvents()
    data class NavigateChat(val storeData: StoreIdentificationData) : StoreEvents()
    data class OpenNavigation(val latLng: LatLng) : StoreEvents()

    data class NavigateToProducts(val storeId: Int, val storeName: String) : StoreEvents()

    data class NavigateToProduct(val productId: Int, val productName: String) : StoreEvents()
}