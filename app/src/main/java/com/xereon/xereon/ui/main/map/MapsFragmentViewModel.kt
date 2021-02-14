package com.xereon.xereon.ui.main.map

import android.service.autofill.SaveInfo
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.xereon.xereon.R
import com.xereon.xereon.data.location.MapsData
import com.xereon.xereon.data.location.source.MapsDataProvider
import com.xereon.xereon.data.model.LocationStore
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.data.repository.PlacesRepository
import com.xereon.xereon.network.response.PlacesRequest
import com.xereon.xereon.network.response.PlacesResponse
import com.xereon.xereon.ui.map.MapViewModel
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Resource
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.ui.notifyObserver
import com.xereon.xereon.util.viewmodel.XereonViewModel
import kotlinx.coroutines.launch
import java.lang.Error
import java.lang.Exception

class MapsFragmentViewModel @ViewModelInject constructor(
    private val mapsDataProvider: MapsDataProvider,
    private val placesRepository: PlacesRepository
) : XereonViewModel() {

    val exception: SingleLiveEvent<Int> = SingleLiveEvent()

    private val _loadedStores: MutableLiveData<ArrayList<LocationStore>> = MutableLiveData()
    val loadedStores: LiveData<ArrayList<LocationStore>> get() = _loadedStores

    private val _places: MutableLiveData<PlacesResponse> = MutableLiveData()
    val places: LiveData<PlacesResponse> get() = _places

    private val _loadedPostCodes = arrayListOf<String>()

    val mapsPosition : LiveData<MapsData> = mapsDataProvider.currentMapsPosition.asLiveData()

    fun saveMapsPosition(mapsData: MapsData) = mapsDataProvider.saveToCache(mapsData)

    fun loadStoresInRegion(postCode: String, initial: Boolean = false) {
        if (initial && _loadedPostCodes.isNotEmpty()) {
            _loadedStores.notifyObserver()
            return
        }

        val shortedPostCode = postCode.substring(0, 4)
        if (_loadedPostCodes.contains(shortedPostCode))
            return

        launch {
            try {
                _loadedPostCodes.add(postCode)
                when (val response = mapsDataProvider.getStoresInRegion(postCode)) {
                    is Resource.Success -> {
                        if (_loadedStores.value == null)
                            _loadedStores.value = ArrayList( response.data ?: emptyList() )
                        else {
                            _loadedStores.value!!.apply {

                                if (size > MAX_STORES_LOADED) {
                                    clear()
                                    _loadedPostCodes.clear()
                                    _loadedPostCodes.add(shortedPostCode)
                                }

                                addAll(response.data!!)
                            }
                            _loadedStores.notifyObserver()
                        }
                    }
                    is Resource.Error -> {
                        _loadedPostCodes.remove(postCode)
                        exception.postValue(response.message!!)
                    }
                }
            } catch (e: Exception) { exception.postValue(R.string.unexpected_exception) }
        }
    }

    fun autocompletePlacesSearch(query: String) = launch {
        try {
            val request = PlacesRequest(query = query, hitsPerPage = 4)
            when (val response = placesRepository.getPlaces(request)) {
                is Resource.Success -> _places.value = response.data
                is Resource.Error -> {
                    exception.postValue(response.message!!)
                    _places.value = PlacesResponse(emptyList(), 0, 0, "")
                }
            }
        } catch (e: Exception) { exception.postValue(R.string.unexpected_exception) }
    }

    companion object {
        private const val MAX_STORES_LOADED = 200
    }
}