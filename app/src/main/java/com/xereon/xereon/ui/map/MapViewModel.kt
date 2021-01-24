package com.xereon.xereon.ui.map

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.xereon.xereon.data.model.LocationStore
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.repository.PlacesRepository
import com.xereon.xereon.data.repository.StoreRepository
import com.xereon.xereon.network.response.PlacesRequest
import com.xereon.xereon.network.response.PlacesResponse
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Constants.DEFAULT_LAT
import com.xereon.xereon.util.Constants.DEFAULT_LNG
import com.xereon.xereon.util.Constants.DEFAULT_ZOOM
import com.xereon.xereon.util.Constants.MAX_NUMBER_STORES_ON_MAP
import com.xereon.xereon.util.Resource
import com.xereon.xereon.util.view_utils.notifyObserver
import kotlinx.coroutines.launch
import java.lang.Exception

class MapViewModel @ViewModelInject constructor(
    private val storeRepository: StoreRepository,
    private val placesRepository: PlacesRepository
) : ViewModel() {

    sealed class MapStoreEvent {
        class Success(val storeData: Store) : MapStoreEvent()
        class Failure(val errorText: String) : MapStoreEvent()
        object Loading : MapStoreEvent()
    }

    /* the places response */
    private val _places: MutableLiveData<PlacesResponse> = MutableLiveData()
    val places: LiveData<PlacesResponse> get() = _places

    /* the selected store */
    private val _selectedStore: MutableLiveData<MapStoreEvent> = MutableLiveData()
    val selectedStore: LiveData<MapStoreEvent> get() = _selectedStore

    /* all the loaded zip-codes */
    private val _loadedZips = arrayListOf<String>()

    /* a list of all loaded stores */
    private val _loadedStores = MutableLiveData<ArrayList<LocationStore>>()
    val loadedStores: LiveData<ArrayList<LocationStore>> get() = _loadedStores

    /* the camera position  */
    private var cameraPosition = CameraPosition.fromLatLngZoom(LatLng(DEFAULT_LAT, DEFAULT_LNG), DEFAULT_ZOOM)
    fun getCameraPosition() = cameraPosition


    fun getStores(zip: String, initialCall: Boolean = false) {
        try {
            //cause the list is saved in an livedata, it gets not observed on first load
            if (_loadedZips.size > 0 && initialCall) {
                _loadedStores.notifyObserver()
                return
            }

            //check, if zip isn't already loaded
            val shortedZip = zip.substring(0, 4)
            if (_loadedZips.contains(shortedZip))
                return

            viewModelScope.launch {
                _loadedZips.add(shortedZip)

                when (val response = storeRepository.getStoresInArea(zip)) {
                    is Resource.Success -> {
                        if (_loadedStores.value == null) {
                            _loadedStores.value = ArrayList(response.data ?: emptyList())
                        } else {
                            _loadedStores.value!!.apply {
                                // if more than MAX_NUMBER_STORES_ON_MAP are inserted, all loaded Stores are cleared
                                if (this.size > MAX_NUMBER_STORES_ON_MAP) {
                                    clear()
                                    _loadedZips.clear()
                                    _loadedZips.add(shortedZip)
                                }

                                addAll(response.data ?: emptyList())
                            }
                            _loadedStores.notifyObserver()
                        }
                    }
                    is Resource.Error -> {
                        _loadedZips.remove(shortedZip)
                    }
                }

            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in MapViewModel: ${e.message}")
        }
    }

    fun getStore(storeId: Int) {
        try {
            viewModelScope.launch {
                _selectedStore.value = MapStoreEvent.Loading
                when (val response = storeRepository.getStore(storeId)) {
                    is Resource.Success -> _selectedStore.value =
                        MapStoreEvent.Success(response.data!!)
                    is Resource.Error -> _selectedStore.value =
                        MapStoreEvent.Failure(response.message!!)
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in MapViewModel: ${e.message}")
        }
    }

    fun autocompletePlace(query: String) {
        try {
            viewModelScope.launch {
                val request = PlacesRequest(
                    query = query,
                    hitsPerPage = 4
                )
                when (val response = placesRepository.getPlaces(request)) {
                    is Resource.Success -> _places.value = response.data
                    is Resource.Error -> {
                        Log.e(
                            Constants.TAG,
                            "Unexpected error in MapViewModel: ${response.message}"
                        )
                        _places.value = PlacesResponse(emptyList(), 0, 0, "")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(Constants.TAG, "Unexpected error in MapViewModel: ${e.message}")
        }
    }

    fun saveCameraPosition(cameraPosition: CameraPosition?) {
        if (cameraPosition != null)
            this.cameraPosition = cameraPosition
    }
}