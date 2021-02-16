package com.xereon.xereon.ui.main.map

import android.location.Geocoder
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.android.clustering.ClusterManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.search.PlacesAdapter
import com.xereon.xereon.data.category.util.CategoryConverter
import com.xereon.xereon.data.maps.MapsData
import com.xereon.xereon.data.maps.Place
import com.xereon.xereon.data.store.LocationStore
import com.xereon.xereon.data.store.Store
import com.xereon.xereon.databinding.FragmentMapBinding
import com.xereon.xereon.ui.main.MainActivity
import com.xereon.xereon.util.map.ClusterRenderer
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.ui.doNavigate
import com.xereon.xereon.util.ui.observeLiveData
import com.xereon.xereon.util.ui.showError
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception
import java.lang.NullPointerException
import java.util.*

@AndroidEntryPoint
class MapFragment : Fragment(R.layout.fragment_map) {
    private val viewModel by viewModels<MapsFragmentViewModel>()

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var googleMap: GoogleMap
    private lateinit var clusterManager: ClusterManager<LocationStore>
    private lateinit var searchView: SearchView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    private val placesAdapter = PlacesAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentMapBinding.bind(view)
        binding.googleMaps.onCreate(savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.mapBottomSheet)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.apply {
            googleMaps.getMapAsync {
                googleMap = it
                setupMapIfReady(googleMap)

                viewModel.mapsPosition.observeLiveData(this@MapFragment) {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it.latLng, it.zoom))

                    if (it.postCode.isNotEmpty()) {
                        binding.loadStores.isVisible = true
                        viewModel.loadStoresInRegion(it.postCode)
                    }
                }
                viewModel.loadedStores.observeLiveData(this@MapFragment) {
                    binding.loadStores.isVisible = false
                    if (::clusterManager.isInitialized) {
                        clusterManager.addItems(it)
                        clusterManager.cluster()
                    }
                }
            }

            placesRecycler.apply {
                adapter = placesAdapter
                itemAnimator = null
                layoutManager = LinearLayoutManager(requireContext())
            }

            storeMoreInformation.setOnClickListener { viewModel.seeMoreClick() }
        }

        placesAdapter.setOnItemClickListener(object: PlacesAdapter.ItemClickListener{
            override fun onItemClick(place: Place) {
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 13f))
                binding.placesRecycler.isVisible = false
                searchView.clearFocus()
            }
        })

        viewModel.places.observeLiveData(this) { placesAdapter.submitList(it.hits) }

        viewModel.exception.observeLiveData(this) {
            showError(it)
            binding.loadStores.isVisible = false
            binding.storeLoading.isVisible = false
        }

        viewModel.store.observeLiveData(this) { fillBottomSheet(it) }

        viewModel.events.observeLiveData(this) {
            when(it) {
                is MapEvents.NavigateToStore ->
                    doNavigate(
                        MapFragmentDirections.actionMapFragmentToStoreFragment(it.storeId, it.storeName)
                    )
                is MapEvents.LoadStoresInRegion ->
                    binding.loadStores.isVisible = true
            }
        }

        setupMenu()
    }

    private fun setupMapIfReady(googleMap: GoogleMap) {
        googleMap.apply {

            clusterManager = ClusterManager(requireContext(), googleMap)
            clusterManager.renderer = ClusterRenderer(requireContext(), googleMap, clusterManager)

            setOnCameraIdleListener {
                clusterManager.onCameraIdle()

                val postCode = getPostCode(cameraPosition.target)
                if (postCode.isNotEmpty())
                    viewModel.loadStoresInRegion(postCode)
            }

            setOnMarkerClickListener(clusterManager)
            clusterManager.setOnClusterItemClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                fillBottomSheet(it)
                viewModel.getStoreData(it.id)
                false
            }

            setOnMapClickListener {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                searchView.clearFocus()
            }

            uiSettings.apply {
                isRotateGesturesEnabled = false
                isTiltGesturesEnabled = false
                isMapToolbarEnabled = false
            }
            setMinZoomPreference(Constants.MIN_ZOOM)

            setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.custom_map_style))
        }
    }

    private fun getPostCode(latLng: LatLng): String = try {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        address[0].postalCode
    } catch (e: Exception) {
        when (e) {
            is NullPointerException, is IndexOutOfBoundsException -> { String() }
            else -> {
                showError(R.string.no_connection_exception)
                String()
            }
        }
    }

    private fun setupMenu() {
        binding.toolbar.apply {
            inflateMenu(R.menu.menu_map)
            searchView = menu.findItem(R.id.menu_item_search_location).actionView as SearchView

            searchView.queryHint = "Stadt suchen"
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?) = true

                override fun onQueryTextChange(query: String?): Boolean {
                    if (query.isNullOrEmpty())
                        binding.placesRecycler.isVisible = false
                    else {
                        viewModel.autocompletePlacesSearch(query)
                        binding.placesRecycler.isVisible = true
                    }
                    return true
                }
            })
            searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    searchView.clearFocus()
                    binding.placesRecycler.isVisible = false
                    (requireActivity() as MainActivity).setBottomNavBarVisibility(true)
                } else {
                    (requireActivity() as MainActivity).setBottomNavBarVisibility(false)
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }
            }
        }
    }

    private fun fillBottomSheet(store: LocationStore) {
        binding.apply {
            storeLoading.isVisible = true
            storeName.text = store.name
            Glide.with(requireContext()).load(store.logoImageURL).into(storeImage)
            storeType.text = store.type
            storeType.setTextColor(requireContext().getColor(CategoryConverter.getCategoryColor(store.category)))
        }
    }

    private fun fillBottomSheet(store: Store) {
        binding.apply {
            storeLoading.isVisible = false
            storeAddress.text = store.completeAddress

            if (store.openinghours.size >= 7) {
                val dayOfWeek = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)+5)%7
                val openingTime = if (store.openinghours[dayOfWeek].isBlank())
                    "geschlossen"
                else
                    store.openinghours[dayOfWeek]

                val openingString = resources.getStringArray(R.array.day_of_week_short)[dayOfWeek] +
                        ".: $openingTime"
                storeOpening.text = openingString
            } else
                storeOpening.text = "-"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            val cameraLatLng = googleMap.cameraPosition.target
            val postCode = getPostCode(cameraLatLng)
            viewModel.saveMapsPosition(
                MapsData(
                    cameraLatLng.latitude.toFloat(),
                    cameraLatLng.longitude.toFloat(),
                    googleMap.cameraPosition.zoom,
                    postCode
                )
            )
            binding.googleMaps.onDestroy()
        } catch (ignore: Exception) {
        }
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        try {
            binding.googleMaps.onStart()
        } catch (ignore: Exception) {
        }
    }
    override fun onResume() {
        super.onResume()
        try {
            binding.googleMaps.onResume()
        } catch (ignore: Exception) {
        }
    }
    override fun onPause() {
        super.onPause()
        try {
            binding.googleMaps.onPause()
        } catch (ignore: Exception) {
        }
    }
    override fun onStop() {
        super.onStop()
        try {
            binding.googleMaps.onStop()
        } catch (ignore: Exception) {
        }
    }
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        try {
            binding.googleMaps.onSaveInstanceState(outState)
        } catch (ignore: Exception) {
        }
    }
    override fun onLowMemory() {
        super.onLowMemory()
        try {
            binding.googleMaps.onLowMemory()
        } catch (ignore: Exception) {
        }
    }
}