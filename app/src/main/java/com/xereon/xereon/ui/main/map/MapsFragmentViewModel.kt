package com.xereon.xereon.ui.main.map

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.xereon.xereon.R
import com.xereon.xereon.data.maps.MapsData
import com.xereon.xereon.data.maps.source.MapsDataProvider
import com.xereon.xereon.data.store.LocationStore
import com.xereon.xereon.data.repository.PlacesRepository
import com.xereon.xereon.data.store.Store
import com.xereon.xereon.network.response.PlacesRequest
import com.xereon.xereon.network.response.PlacesResponse
import com.xereon.xereon.util.Resource
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.ui.notifyObserver
import com.xereon.xereon.util.viewmodel.XereonViewModel
import java.lang.Exception

class MapsFragmentViewModel @ViewModelInject constructor(
    private val mapsDataProvider: MapsDataProvider,
    private val placesRepository: PlacesRepository
) : XereonViewModel() {

    val exception: SingleLiveEvent<Int> = SingleLiveEvent()
    val events: SingleLiveEvent<MapEvents> = SingleLiveEvent()

    private val _loadedStores: MutableLiveData<ArrayList<LocationStore>> = MutableLiveData()
    val loadedStores: LiveData<ArrayList<LocationStore>> get() = _loadedStores

    private val _places: MutableLiveData<PlacesResponse> = MutableLiveData()
    val places: LiveData<PlacesResponse> get() = _places

    private val _store: MutableLiveData<Store> = MutableLiveData()
    val store: LiveData<Store> get() = _store

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

        events.postValue(MapEvents.LoadStoresInRegion)

        launch {
            try {
                _loadedPostCodes.add(shortedPostCode)
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
                        _loadedPostCodes.remove(shortedPostCode)
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

    fun getStoreData(storeId: Int) = launch {
        when (val response = mapsDataProvider.getStoreData(storeId)) {
            is Resource.Success -> _store.value = response.data!!
            is Resource.Error -> exception.postValue(response.message!!)
        }
    }

    fun seeMoreClick() {
        val currentStore = _store.value
        if (currentStore != null)
            events.postValue(MapEvents.NavigateToStore(currentStore.id, currentStore.name))
        else
            exception.postValue(R.string.no_connection_exception)
    }

    companion object {
        private const val MAX_STORES_LOADED = 200
    }
}