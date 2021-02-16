package com.xereon.xereon.ui.selectLocation

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.xereon.xereon.R
import com.xereon.xereon.data.maps.Place
import com.xereon.xereon.data.repository.PlacesRepository
import com.xereon.xereon.network.response.PlacesRequest
import com.xereon.xereon.network.response.PlacesResponse
import com.xereon.xereon.util.Resource
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel
import java.lang.Exception

class SelectLocationFragmentViewModel @ViewModelInject constructor(
    private val placesRepository: PlacesRepository,
) : XereonViewModel() {

    val events: SingleLiveEvent<SelectLocationEvents> = SingleLiveEvent()

    private val _places: MutableLiveData<PlacesResponse> = MutableLiveData()
    val places: LiveData<PlacesResponse> get() = _places

    var selectedPlaceData: Any? = null

    fun onBackButtonClick() {
        events.postValue(SelectLocationEvents.NavigateBack)
    }

    fun onLocationSelectionFinished() {
        events.postValue(SelectLocationEvents.NavigateToMainActivity)
    }

    fun onLocationSelected(place: Place) {
        events.postValue(SelectLocationEvents.LocationSelected(place))
        selectedPlaceData = place
    }

    init {
        launch {
            try {
                events.postValue(SelectLocationEvents.Loading)
                when (val response = placesRepository.getApproximatePosition()) {
                    is Resource.Success -> {
                        events.postValue(SelectLocationEvents.Success(response.data!!))
                        selectedPlaceData = response.data
                    }
                    is Resource.Error ->
                        events.postValue(SelectLocationEvents.Error(response.message!!))
                }
            } catch (e: Exception) {
                events.postValue(SelectLocationEvents.Error(R.string.unexpected_exception))
            }
        }
    }

    fun autocompletePlace(query: String) {
        launch {
            try {
                val request = PlacesRequest(query = query, hitsPerPage = 4)
                when (val response = placesRepository.getPlaces(request)) {
                    is Resource.Success ->
                        _places.value = response.data
                    is Resource.Error -> {
                        events.postValue(SelectLocationEvents.Error(response.message!!))
                        _places.value = PlacesResponse(emptyList(), 0, 0, query)
                    }
                }
            } catch (e: Exception) {
                events.postValue(SelectLocationEvents.Error(R.string.unexpected_exception))
            }
        }
    }

}