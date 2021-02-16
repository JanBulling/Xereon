package com.xereon.xereon.ui.selectLocation

import com.xereon.xereon.data.maps.Place
import com.xereon.xereon.data.maps.IPLocation

sealed class SelectLocationEvents {
    object NavigateToMainActivity : SelectLocationEvents()
    object NavigateBack : SelectLocationEvents()

    data class LocationSelected(val place: Place) : SelectLocationEvents()

    data class Error(val message: Int) : SelectLocationEvents()
    data class Success(val data: IPLocation) : SelectLocationEvents()
    object Loading : SelectLocationEvents()
}