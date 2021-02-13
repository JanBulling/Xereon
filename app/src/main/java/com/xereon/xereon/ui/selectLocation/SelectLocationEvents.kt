package com.xereon.xereon.ui.selectLocation

import com.xereon.xereon.data.model.places.Place
import com.xereon.xereon.network.response.IPLocationResponse

sealed class SelectLocationEvents {
    object NavigateToMainActivity : SelectLocationEvents()
    object NavigateBack : SelectLocationEvents()

    data class LocationSelected(val place: Place) : SelectLocationEvents()

    data class Error(val message: Int) : SelectLocationEvents()
    data class Success(val data: IPLocationResponse) : SelectLocationEvents()
    object Loading : SelectLocationEvents()
}