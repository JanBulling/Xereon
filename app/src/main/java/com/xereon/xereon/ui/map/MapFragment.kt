package com.xereon.xereon.ui.map

import android.location.Geocoder
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.util.Log.d
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.clustering.ClusterManager
import com.xereon.xereon.R
import com.xereon.xereon.data.model.LocationStore
import com.xereon.xereon.databinding.FrgMapBinding
import com.xereon.xereon.ui.MainActivity
import com.xereon.xereon.ui.store.DefaultStoreFragmentDirections
import com.xereon.xereon.util.ClusterRenderer
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.DataState
import kotlinx.android.synthetic.main.frg_map.*
import java.lang.Exception
import java.util.*

class MapFragment : Fragment(R.layout.frg_map) {
    private val viewModel by activityViewModels<MapViewModel>()

    private var _binding: FrgMapBinding? = null
    private val binding get() = _binding!!

    private var googleMap: GoogleMap? = null
    private var clusterManager: ClusterManager<LocationStore>? = null

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>;
    private var currentStoreId = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FrgMapBinding.bind(view)
        (activity as MainActivity).setBottomNavBarVisibility(true)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.mapBottomSheetBehaviour)
        bottomSheetBehavior.isHideable = true

        mapView?.onCreate(savedInstanceState)

        mapView.getMapAsync {
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
                binding.isLoadingStore = true
                binding.storeName = it.name
                binding.mapBottomSheetBehaviour.visibility = View.VISIBLE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                currentStoreId = it.id
                viewModel.getStoreData(it.id)
                false
            }

            googleMap?.uiSettings?.apply {
                isRotateGesturesEnabled = false
                isTiltGesturesEnabled = false
                isMapToolbarEnabled = false
            }
            googleMap?.setOnMapClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }

            if (savedInstanceState == null)
                googleMap?.moveCamera(CameraUpdateFactory.newCameraPosition(viewModel.getMapPosition()))

            googleMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.custom_map_style))

            viewModel.getStoresInArea("89542", initialCall = true)  //get data, if cluster manager is ready
        }

        map_bottom_store_more_information.setOnClickListener {
            val action = DefaultStoreFragmentDirections.actionToStore(simpleStoreId = currentStoreId)
            findNavController().navigate(action)
        }

        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is DataState.Success -> binding.isLoading = false
                is DataState.Loading -> binding.isLoading = true
                is DataState.Error -> {
                    binding.isLoading = false
                    displayError(dataState.message)
                }
            }
        })
        viewModel.storeData.observe(viewLifecycleOwner, Observer { stores ->
            d(TAG, "received list ${stores.size}")

            clusterManager?.addItems(stores)
            clusterManager?.cluster()
        })
        viewModel.currentStoreData.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is DataState.Success -> {
                    binding.isLoadingStore = false
                    binding.currentStore = dataState.data
                    binding.storeName = dataState.data.name
                    binding.mapBottomSheetBehaviour.visibility = View.VISIBLE
                }
                is DataState.Loading -> binding.isLoadingStore = true
                is DataState.Error -> {
                    binding.isLoadingStore = false
                    displayError(dataState.message)
                }
            }
        })
    }

    fun getPostalCode(latLng: LatLng?): String {
        if (latLng == null)
            return ""
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            return address[0].postalCode
        } catch (e: Exception) {
            displayError("Keine Verbindung...")
        }
        return ""
    }

    private fun displayError(message: String) {
        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        val snackBarView: View = snackBar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.error))
        snackBar.show()
    }

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
        viewModel.saveCurrentMapPosition(googleMap?.cameraPosition)
        mapView?.onDestroy()
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }
}