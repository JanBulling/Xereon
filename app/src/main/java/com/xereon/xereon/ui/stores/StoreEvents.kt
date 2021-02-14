package com.xereon.xereon.ui.stores

sealed class StoreEvents {

    object NavigateBack : StoreEvents()
    object NavigateChat : StoreEvents()
    object OpenNavigation : StoreEvents()

}