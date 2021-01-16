package com.xereon.xereon.ui.map

import android.location.Geocoder
import android.os.Bundle
import android.util.Log.d
import android.util.Log.e
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.search.PlacesAdapter
import com.xereon.xereon.data.model.LocationStore
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.model.places.Place
import com.xereon.xereon.databinding.FrgMapBinding
import com.xereon.xereon.network.PlacesRequest
import com.xereon.xereon.ui._parent.MainActivity
import com.xereon.xereon.ui._parent.OnBackPressedListener
import com.xereon.xereon.ui.store.DefaultStoreFragmentDirections
import com.xereon.xereon.util.ClusterRenderer
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.DataState
import kotlinx.android.synthetic.main.frg_map.*
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*

class MapFragment : Fragment(R.layout.frg_map), OnBackPressedListener, PlacesAdapter.ItemClickListener {
    private val viewModel by activityViewModels<MapViewModel>()

    private var _binding: FrgMapBinding? = null
    private val binding get() = _binding!!

    private var googleMap: GoogleMap? = null
    private var clusterManager: ClusterManager<LocationStore>? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>;
    private var currentStoreId = -1

    private val placesAdapter = PlacesAdapter()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FrgMapBinding.bind(view)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.mapBottomSheetBehaviour)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        mapView?.onCreate(savedInstanceState)

        mapView?.getMapAsync {
            googleMap = it

            clusterManager = ClusterManager(requireContext(), googleMap)
            clusterManager?.renderer = ClusterRenderer(requireContext(), googleMap, clusterManager)

            googleMap?.setOnCameraIdleListener {
                clusterManager?.onCameraIdle()

                val zip = getPostalCode(googleMap?.cameraPosition?.target)
                if (zip.isNotEmpty())
                    viewModel.getStoresInArea(zip)
            }

            googleMap?.setOnMarkerClickListener(clusterManager)
            clusterManager?.setOnClusterItemClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                if (currentStoreId != it.id) {
                    currentStoreId = it.id
                    binding.currentStore = it.toStore()
                    viewModel.getStoreData(it.id)
                }

                false /* show name of store above marker */
            }

            googleMap?.uiSettings?.apply {
                isRotateGesturesEnabled = false
                isTiltGesturesEnabled = false
                isMapToolbarEnabled = false
            }
            googleMap?.setMinZoomPreference(10f)
            googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.custom_map_style))

            /*Hide BottomSheet if clicked on the map*/
            googleMap?.setOnMapClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.isSearching = false
                searchView.clearFocus()
            }

            googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(viewModel.getMapPosition()))

            viewModel.getStoresInArea("89542", initialCall = true)  //get data, if cluster manager is ready
        }

        map_bottom_store_more_information.setOnClickListener {
            val action = DefaultStoreFragmentDirections.actionToStore(simpleStoreId = currentStoreId)
            findNavController().navigate(action)
        }

        map_places_search.apply {
            itemAnimator = null
            adapter = placesAdapter
            setHasFixedSize(true)
        }

        placesAdapter.setOnItemClickListener(this)

        subscribeObserver()

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Ort suchen"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                if (query.isNullOrEmpty())
                    placesAdapter.submitList(emptyList())
                else {
                    viewModel.searchPlace(query)
                    binding.isSearching = true
                }
                return true
            }
        })
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                placesAdapter.submitList(emptyList())
                binding.isSearching = false
                searchView.clearFocus()
            }
        }
    }

    private fun subscribeObserver() {
        viewModel.loadingStateForStores.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                DataState.SUCCESS_INDEX -> binding.isLoading = false
                DataState.LOADING_INDEX -> binding.isLoading = true
                DataState.ERROR_INDEX -> {
                    binding.isLoading = false
                    displayError("Keine Verbindung...")
                }
            }
        })
        viewModel.allStores.observe(viewLifecycleOwner, Observer { stores ->
            clusterManager?.addItems(stores)
            clusterManager?.cluster()
        })
        viewModel.selectedStore.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    binding.isLoadingStore = false
                    binding.currentStore = dataState.data
                    binding.mapBottomSheetBehaviour.visibility = View.VISIBLE
                }
                is DataState.Loading -> binding.isLoadingStore = true
                is DataState.Error -> {
                    binding.isLoadingStore = false
                    displayError(dataState.message)
                }
            }
        })
        viewModel.placesResponse.observe(viewLifecycleOwner, Observer {
            when(it) {
                is DataState.Success -> {
                    placesAdapter.submitList(it.data.hits)
                }
                is DataState.Error -> {
                    e(TAG, "PLACES RESPONSE: ERROR ${it.message}")
                }
            }
        })
    }

    private fun getPostalCode(latLng: LatLng?): String {
        if (latLng == null)
            return ""
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            return address[0].postalCode
        }
        catch (e: Exception) {
            when(e) {
                is NullPointerException, is IndexOutOfBoundsException -> {}
                else -> {
                    e.printStackTrace()
                    displayError("Keine Verbindung...")
                }
            }
        }
        return ""
    }

    private fun displayError(message: String) {
        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        val snackBarView: View = snackBar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.error))
        snackBar.show()
    }

    override fun onBackPressed(): Boolean {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
            return false

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        return true
    }

    override fun onItemClick(place: Place) {
        val location = LatLng(place.coordinares.latitude.toDouble(), place.coordinares.longitude.toDouble())
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(location))
        binding.isSearching = false
        searchView.clearFocus()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }
    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }
    override fun onPause() {
        super.onPause()
        mapView?.onPause()
    }
    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.saveCameraPosition(googleMap?.cameraPosition)
        mapView?.onDestroy()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        mapView?.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}