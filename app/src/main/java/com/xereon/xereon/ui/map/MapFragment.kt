package com.xereon.xereon.ui.map

import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.search.PlacesAdapter
import com.xereon.xereon.data.model.LocationStore
import com.xereon.xereon.data.model.places.Place
import com.xereon.xereon.databinding.FrgMapBinding
import com.xereon.xereon.ui._parent.OnBackPressedListener
import com.xereon.xereon.ui.store.DefaultStoreFragmentDirections
import com.xereon.xereon.util.Constants
import kotlinx.android.synthetic.main.frg_map.*
import kotlinx.coroutines.flow.collect
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*

class MapFragment : Fragment(R.layout.frg_map), OnBackPressedListener, PlacesAdapter.ItemClickListener {
    private val viewModel by activityViewModels<MapViewModel>()

    private var _binding: FrgMapBinding? = null
    private val binding get() = _binding!!

    private var googleMap: GoogleMap? = null
    private var clusterManager: ClusterManager<LocationStore>? = null

    //private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>;
    private var currentStoreId = -1

    private val placesAdapter = PlacesAdapter()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FrgMapBinding.bind(view)

        //bottomSheetBehavior = BottomSheetBehavior.from(binding.mapBottomSheetBehaviour)
        /*bottomSheetBehavior.apply {
            isHideable = true
            state = BottomSheetBehavior.STATE_HIDDEN
        }*/

        binding.mapView.onCreate(savedInstanceState)

        binding.mapView.getMapAsync {
            googleMap = it

            clusterManager = ClusterManager(requireContext(), googleMap)
            clusterManager?.renderer = ClusterRenderer(
                requireContext(),
                googleMap,
                clusterManager
            )

            googleMap?.setOnCameraIdleListener {
                clusterManager?.onCameraIdle()

                val zip = getPostalCode(googleMap?.cameraPosition?.target)
                if (zip.isNotEmpty())
                    viewModel.getStores(zip)
            }

            googleMap?.setOnMarkerClickListener(clusterManager)
            clusterManager?.setOnClusterItemClickListener {
                binding.mapBottomSheetBehaviour.isVisible = true
                //bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                if (currentStoreId != it.id) {
                    currentStoreId = it.id
                    binding.currentStore = it.toStore()
                    viewModel.getStore(it.id)
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
                //bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                binding.mapBottomSheetBehaviour.isVisible = false
                binding.mapPlacesSearch.isVisible = false
                searchView.clearFocus()
            }

            googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(viewModel.getCameraPosition()))

            viewModel.getStores(Constants.DEFAULT_POSTCODE, initialCall = true)  //get data, if cluster manager is ready
        }

        binding.mapBottomStoreMoreInformation.setOnClickListener {
            val action = DefaultStoreFragmentDirections.actionToStore(simpleStoreId = currentStoreId)
            findNavController().navigate(action)
        }

        binding.mapBottomSheetBehaviour.isVisible = viewModel.isBottomSheetVisible

        binding.mapPlacesSearch.apply {
            itemAnimator = null
            adapter = placesAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
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
                    viewModel.autocompletePlace(query)
                    binding.mapPlacesSearch.isVisible = true
                }
                return true
            }
        })
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                placesAdapter.submitList(emptyList())
                binding.mapPlacesSearch.isVisible = false
                searchView.clearFocus()
            }
        }
    }

    private fun subscribeObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect { event ->
                when(event) {
                    is MapViewModel.MapStoreEvent.ShowErrorMessage ->
                        displayError(event.messageId)
                    else -> Unit
                }
            }
        }
        viewModel.loadedStores.observe(viewLifecycleOwner, Observer { stores ->
            clusterManager?.addItems(stores)
            clusterManager?.cluster()
        })
        viewModel.selectedStore.observe(viewLifecycleOwner, Observer { event ->
            when (event) {
                is MapViewModel.MapStoreEvent.Success -> {
                    binding.mapBottomLoading.isVisible = false
                    binding.currentStore = event.storeData
                }
                is MapViewModel.MapStoreEvent.Loading -> binding.mapBottomLoading.isVisible = true
                is MapViewModel.MapStoreEvent.Failure -> {
                    binding.mapBottomLoading.isVisible = false
                }
            }
        })
        viewModel.places.observe(viewLifecycleOwner, Observer { placesResponse ->
            placesAdapter.submitList(placesResponse.hits)
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
                else -> displayError(R.string.no_connection_exception)
            }
        }
        return ""
    }

    private fun displayError(@StringRes messageId: Int) {
        val snackBar = Snackbar.make(requireView(), messageId, Snackbar.LENGTH_LONG)
        val snackBarView: View = snackBar.view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            snackBarView.setBackgroundColor(resources.getColor(R.color.error, null))
        snackBar.show()
    }

    override fun onBackPressed(): Boolean {
        //if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
        if (!binding.mapBottomSheetBehaviour.isVisible)
            return false

        binding.mapBottomSheetBehaviour.isVisible = false
        //bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        return true
    }

    override fun onItemClick(place: Place) {
        val location = LatLng(place.coordinares.latitude.toDouble(), place.coordinares.longitude.toDouble())
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(location))
        binding.mapPlacesSearch.isVisible = false
        searchView.clearFocus()
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()
    }
    override fun onResume() {
        super.onResume()
        binding.mapView.onResume()
    }
    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
    }
    override fun onStop() {
        super.onStop()
        try {
            binding.mapView.onStop()
        }catch (ignore: Exception) { /* NO-OP */ }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.isBottomSheetVisible = binding.mapBottomSheetBehaviour.isVisible
        viewModel.saveCameraPosition(googleMap?.cameraPosition)
        try {
            binding.mapView.onDestroy()
        }catch (ignore: Exception) { /* NO-OP */ }
        _binding = null
    }
    override fun onSaveInstanceState(outState: Bundle) {
        try {
            binding.mapView.onSaveInstanceState(outState)
        }catch (ignore: Exception) { /* NO-OP */ }

        super.onSaveInstanceState(outState)
    }
    override fun onLowMemory() {
        super.onLowMemory()
        try {
            binding.mapView.onLowMemory()
        }catch (ignore: Exception) { /* NO-OP */ }
    }
}