package com.xereon.xereon.ui.login

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.adapter.search.PlacesAdapter
import com.xereon.xereon.data.model.places.Place
import com.xereon.xereon.databinding.FrgChooseLocationBinding
import com.xereon.xereon.di.ProvideApplicationState
import com.xereon.xereon.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class ChooseLocationFragment : Fragment(R.layout.frg_choose_location), PlacesAdapter.ItemClickListener {
    private val viewModel: LoginViewModel by viewModels()

    private var _binding: FrgChooseLocationBinding? = null
    private val binding get() = _binding!!

    private val placesAdapter = PlacesAdapter()

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject @ProvideApplicationState
    lateinit var applicationState: Constants.ApplicationState

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgChooseLocationBinding.bind(view)

        binding.apply {
            chooseLocationPlacesAutocomplete.apply {
                itemAnimator = null
                adapter = placesAdapter
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
            }

            chooseLocationSearch.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    chooseLocationSearch.clearFocus()
                    return true
                }

                override fun onQueryTextChange(query: String?): Boolean {
                    if (query.isNullOrEmpty())
                        placesAdapter.submitList(emptyList())
                    else {
                        viewModel.autocompletePlacesSearch(query)
                        binding.chooseLocationPlacesAutocomplete.isVisible = true
                    }
                    return true
                }
            })
            chooseLocationSearch.setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    placesAdapter.submitList(emptyList())
                    binding.chooseLocationPlacesAutocomplete.isVisible = false
                    chooseLocationSearch.clearFocus()
                }
            }

            chooseLocationFinish.setOnClickListener {
                val city = viewModel.selectedCityName
                val postCode = viewModel.selectedPostCode
                val lat = viewModel.selectedLocationLat
                val lng = viewModel.selectedLocationLng

                if (city.isNotBlank() && postCode.isNotBlank() && lat != 0f && lng != 0f) {
                    preferences.edit()
                        .putString(Constants.PREF_LOCATION_CITY, city)
                        .putString(Constants.PREF_LOCATION_POSTCODE, postCode)
                        .putFloat(Constants.PREF_LOCATION_LAT, lat)
                        .putFloat(Constants.PREF_LOCATION_LNG, lng)
                        .apply()

                    when (applicationState) {
                        /*skipped and no location*/
                        Constants.ApplicationState.SKIPPED_NO_LOCATION ->
                            preferences.edit().putInt(Constants.PREF_APPLICATION_STATE,
                                Constants.ApplicationState.SKIPPED_HAS_LOCATION.index).apply()

                        /*not valid and no location*/
                        Constants.ApplicationState.LOGGED_IN_NO_LOCATION_NOT_VALID ->
                            preferences.edit().putInt(Constants.PREF_APPLICATION_STATE,
                                Constants.ApplicationState.LOGGED_IN_HAS_LOCATION_NOT_VALID.index).apply()

                        /*VALID but no location yet*/
                        Constants.ApplicationState.LOGGED_IN_NO_LOCATION_VALID ->
                            preferences.edit().putInt(Constants.PREF_APPLICATION_STATE,
                                Constants.ApplicationState.VALID_USER_ACCOUNT.index).apply()

                        else ->
                            preferences.edit().putInt(Constants.PREF_APPLICATION_STATE,
                                Constants.ApplicationState.SKIPPED_HAS_LOCATION.index).apply()
                    }

                    findNavController().navigate(R.id.action_ChooseLocation_to_Explore)
                } else
                    showError(R.string.no_location_exception)
            }

        }

        placesAdapter.setOnItemClickListener(this)

        viewModel.getApproximateDataWithIPAddress()
        subscribeToObserver()
    }

    private fun subscribeToObserver() {
        viewModel.places.observe(viewLifecycleOwner, Observer {
            placesAdapter.submitList(it.hits)
        })


        viewModel.ipPlace.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LoginViewModel.LoginEvent.SuccessIPLocation -> {
                    binding.chooseLocationLoading.isVisible = false
                    binding.apply {
                        chooseLocationSelectedCity.text = it.ipLocation.city
                        chooseLocationSelectedRegion.text = it.ipLocation.region
                        chooseLocationSelectedZip.text = it.ipLocation.postCode
                    }
                }
                else ->
                    binding.chooseLocationLoading.isVisible = false
            }

        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect {
                if (it is LoginViewModel.LoginEvent.LoginError)
                    showError(it.messageId)
            }
        }
    }

    override fun onItemClick(place: Place) {
        binding.apply {
            chooseLocationSelectedCity.text = place.name
            chooseLocationSelectedRegion.text = place.administrative
            chooseLocationSelectedZip.text = place.postCode
            chooseLocationPlacesAutocomplete.isVisible = false
            chooseLocationSearch.clearFocus()
        }
        viewModel.selectedCityName = place.name
        viewModel.selectedPostCode = place.postCode
        viewModel.selectedLocationLat = place.coordinates.latitude
        viewModel.selectedLocationLng = place.coordinates.longitude
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