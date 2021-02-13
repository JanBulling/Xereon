package com.xereon.xereon.ui.selectLocation

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.adapter.search.PlacesAdapter
import com.xereon.xereon.data.model.places.Place
import com.xereon.xereon.databinding.FragmentSelectLocationBinding
import com.xereon.xereon.network.response.IPLocationResponse
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SelectLocationFragment : Fragment(R.layout.fragment_select_location) {
    private val viewModel by viewModels<SelectLocationFragmentViewModel>()

    private var _binding: FragmentSelectLocationBinding? = null
    private val binding get() = _binding!!

    private val placesAdapter = PlacesAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSelectLocationBinding.bind(view)

        binding.apply {
            selectLocationSearchInput.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?) = false

                override fun onQueryTextChange(newText: String?): Boolean {
                    viewModel.autocompletePlace(newText ?: "")
                    return true
                }
            })
            selectLocationSearchInput.setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (!hasFocus)
                    selectLocationSearchInput.clearFocus()
            }
            selectLocationFinishBtn.setOnClickListener { viewModel.onLocationSelectionFinished() }
            selectLocationPlacesRecycler.apply {
                itemAnimator = null
                adapter = placesAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                isVisible = false
            }
        }

        placesAdapter.setOnItemClickListener(object: PlacesAdapter.ItemClickListener{
            override fun onItemClick(place: Place) {
                viewModel.onLocationSelected(place)
            }
        })

        viewModel.places.observe(viewLifecycleOwner, Observer {
            binding.selectLocationPlacesRecycler.isVisible = true
            placesAdapter.submitList(it.hits)
        })
        viewModel.events.observe(viewLifecycleOwner, Observer {
            when(it) {
                SelectLocationEvents.Loading ->
                    binding.selectLocationLoading.isVisible = true
                is SelectLocationEvents.Error -> {
                    binding.selectLocationLoading.isVisible = false
                    showError(it.message)
                }
                is SelectLocationEvents.Success -> {
                    binding.selectLocationLoading.isVisible = false
                    bindPlacesData(it.data)
                }
                is SelectLocationEvents.LocationSelected -> {
                    binding.selectLocationPlacesRecycler.isVisible = false
                    binding.selectLocationSearchInput.clearFocus()
                    bindPlacesData(it.place)
                }

                SelectLocationEvents.NavigateToMainActivity -> {
                    when (val data = viewModel.selectedPlaceData) {
                        is Place ->
                            (requireActivity() as SelectLocationActivity).completeSelectLocation(data)
                        is IPLocationResponse ->
                            (requireActivity() as SelectLocationActivity).completeSelectLocation(data)
                        else ->
                            showError(R.string.no_location_exception)
                    }

                }
            }
        })
    }

    private fun bindPlacesData(place: Place) {
        binding.apply {
            selectLocationCity.text = place.name
            selectLocationRegion.text = place.administrative
            selectLocationPostcode.text = place.postCode
        }
    }

    private fun bindPlacesData(place: IPLocationResponse) {
        binding.apply {
            selectLocationCity.text = place.city
            selectLocationRegion.text = place.region
            selectLocationPostcode.text = place.postCode
        }
    }

    private fun showError(@StringRes messageId: Int) {
        val snackbar = Snackbar.make(requireView(), messageId, Snackbar.LENGTH_LONG);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            snackbar.view.setBackgroundColor(resources.getColor(R.color.type_azure, null))
        snackbar.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}