package com.xereon.xereon.ui.map

import android.util.Log.d
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.xereon.xereon.data.model.LocationStore
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.repository.PlacesRepository
import com.xereon.xereon.data.repository.StoreRepository
import com.xereon.xereon.network.PlacesRequest
import com.xereon.xereon.network.PlacesResponse
import com.xereon.xereon.util.Constants.DEFAULT_LAT
import com.xereon.xereon.util.Constants.DEFAULT_LNG
import com.xereon.xereon.util.Constants.DEFAULT_ZOOM
import com.xereon.xereon.util.Constants.MAX_NUMBER_STORES_ON_MAP
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.DataState
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class MapViewModel @ViewModelInject constructor(
    private val storeRepository: StoreRepository,
    private val placesRepository: PlacesRepository
) : ViewModel() {

    private var cameraPosition = CameraPosition.fromLatLngZoom(LatLng(DEFAULT_LAT, DEFAULT_LNG), DEFAULT_ZOOM)
    private var selectedStoreId: Int = -1

    private val _loadedZips: MutableList<String> = mutableListOf()
    private val _allStores: MutableLiveData<MutableList<LocationStore>> = MutableLiveData(mutableListOf())
    private val _loadingStateForStores: MutableLiveData<Int> = MutableLiveData(DataState.LOADING_INDEX)
    private val _selectedStore: MutableLiveData<DataState<Store>> = MutableLiveData()
    private val _placesResponse: MutableLiveData<DataState<PlacesResponse>> = MutableLiveData()

    //getter
    val selectedStore : LiveData<DataState<Store>> get() = _selectedStore
    val loadingStateForStores : LiveData<Int> get() = _loadingStateForStores
    val allStores : LiveData<MutableList<LocationStore>> get() = _allStores
    fun getMapPosition() = cameraPosition ?: CameraPosition.fromLatLngZoom(LatLng(DEFAULT_LAT, DEFAULT_LNG), DEFAULT_ZOOM)
    val placesResponse : LiveData<DataState<PlacesResponse>> get() = _placesResponse


    fun getStoreData(storeId: Int) {
        if (selectedStoreId != storeId) {
            viewModelScope.launch {

                storeRepository.getStoreData(storeId).onEach { dataState ->
                    _selectedStore.value = dataState
                    selectedStoreId = storeId
                }.launchIn(viewModelScope)

            }
        } else
            _selectedStore.notifyObserver()
    }

    fun getStoresInArea(zip: String, isRetry: Boolean = false, initialCall: Boolean = false) {
        //because in _allStores is a list saved, it does not update automatically (only content of
        // the list is changed, not list it self)
        if (_loadedZips.size > 0 && initialCall) {
            _allStores.notifyObserver()
            return
        }

        //If the stores in the zip area are already loaded
        if (!isRetry && _loadedZips.contains(zip.substring(0, 4)))
            return

        viewModelScope.launch {

            _loadedZips.add(zip.substring(0, 4)) /*Need to add zip before loading. Otherwise it coul load same area twice*/

            storeRepository.getStoresInArea(zip).onEach { data ->
                when (data) {

                    is DataState.Success -> {
                        _loadingStateForStores.value = DataState.SUCCESS_INDEX

                        _allStores.value?.apply {

                            if (size > MAX_NUMBER_STORES_ON_MAP) { //if more stores are loaded, clear all and also clear loaded postal codes
                                clear()

                                _loadedZips.clear()
                                _loadedZips.add(zip.substring(0, 4))
                            }

                            addAll(data.data)
                            _allStores.notifyObserver()
                        }
                    }

                    is DataState.Error -> {
                        _loadingStateForStores.value = DataState.ERROR_INDEX
                        _loadedZips.remove(zip.substring(0, 4))  /*casue it was added earlier*/
                    }

                    is DataState.Loading -> _loadingStateForStores.value = DataState.LOADING_INDEX
                }
            }.launchIn(viewModelScope)

        }
    }

    fun saveCameraPosition(cameraPosition: CameraPosition?) {
        if (cameraPosition != null)
            this.cameraPosition = cameraPosition
    }

    fun searchPlace(query: String) {
        viewModelScope.launch {
            val request = PlacesRequest(query = query, hitsPerPage = 4)

            placesRepository.getPlaces(request).onEach { dataState ->
                _placesResponse.value = dataState
            }.launchIn(viewModelScope)

        }
    }

    /*For updating a mutable live data on purpose*/
    fun <T> MutableLiveData<T>.notifyObserver() { this.value = this.value }
}