package com.xereon.xereon.ui.search

import android.app.AlertDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import com.xereon.xereon.R
import com.xereon.xereon.adapter.loadStateAdapter.StoresLoadStateAdapter
import com.xereon.xereon.adapter.pagingAdapter.StoresPagingAdapter
import com.xereon.xereon.adapter.search.PlacesAdapter
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.data.model.places.Place
import com.xereon.xereon.data.util.CategoryUtils
import com.xereon.xereon.databinding.FrgSearchBinding
import com.xereon.xereon.ui._parent.MainActivity
import com.xereon.xereon.ui._parent.OnBackPressedListener
import com.xereon.xereon.ui.store.DefaultStoreFragmentDirections
import com.xereon.xereon.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.frg_search), OnBackPressedListener {
    private val viewModel by activityViewModels<SearchViewModel>()

    private var _binding: FrgSearchBinding? = null
    private val binding get() = _binding!!

    private val storeAdapter = StoresPagingAdapter()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgSearchBinding.bind(view)

        storeAdapter.setOnItemClickListener(object : StoresPagingAdapter.ItemClickListener {
            override fun onItemClick(simpleStore: SimpleStore) {
                val action = DefaultStoreFragmentDirections.actionToStore(simpleStore)
                findNavController().navigate(action)
            }
        })
        storeAdapter.addLoadStateListener { loadStates ->
            binding.apply {
                searchLoading.isVisible = loadStates.source.refresh is LoadState.Loading   //initial
                searchError.isVisible = loadStates.source.refresh is LoadState.Error

                //empty view
                searchNoResults.isVisible = loadStates.source.refresh is LoadState.NotLoading &&
                        loadStates.append.endOfPaginationReached &&
                        storeAdapter.itemCount < 1
            }
        }
        binding.buttonRetry.setOnClickListener { storeAdapter.retry() }

        binding.searchResults.apply {
            setHasFixedSize(true)
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
            adapter = storeAdapter.withLoadStateFooter(
                footer = StoresLoadStateAdapter { storeAdapter.retry() }
            )
        }

        setupFilter()

        subscribeToObserver()

        setHasOptionsMenu(true)
    }

    private fun subscribeToObserver() {
        viewModel.searchResponse.observe(viewLifecycleOwner, Observer {
            storeAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            binding.searchEmpty.isVisible = false
        })
    }

    private fun setupFilter() {
        binding.apply {
            searchSortingBackground.setOnClickListener { hideFilterPanel() }
            searchSearchBtn.setOnClickListener { performSearch() }

            searchSortRadioGroup.setOnCheckedChangeListener { _, id ->
                val searchSorting = when (id) {
                    R.id.search_sort_new_first -> Constants.SortTypes.SORT_RESPONSE_NEW_FIRST
                    R.id.search_sort_old_first -> Constants.SortTypes.SORT_RESPONSE_OLD_FIRST
                    R.id.search_sort_A_Z -> Constants.SortTypes.SORT_RESPONSE_A_Z
                    R.id.search_sort_Z_A -> Constants.SortTypes.SORT_RESPONSE_Z_A
                    else -> Constants.SortTypes.SORT_RESPONSE_NEW_FIRST
                }

                viewModel.sorting = searchSorting
            }

            val categoriesChipGroup = binding.searchChipGroupSelectFilter
            for (i in 0 until (CategoryUtils.getNumberOfCategories() - 1)) {
                val chip = Chip(requireContext())
                chip.text = CategoryUtils.getCategoryName(i)
                chip.isCheckable = true

                categoriesChipGroup.addView(chip)
                chip.setOnClickListener {
                    if (chip.isChecked) //gets checked or unchecked first, so test for opposite
                        viewModel.category = i
                }

                if (i == viewModel.category)    //check button by default
                    categoriesChipGroup.check(chip.id)
            }
            categoriesChipGroup.setOnCheckedChangeListener { _, id ->
                if (id == View.NO_ID)
                    viewModel.category = -1
            }

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_search_fragment, menu)

        searchView = (requireActivity() as MainActivity).getSearch()
        searchView.queryHint = "Filiale suchen"

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                storeAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                performSearch()
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                viewModel.queryText = query ?: ""
                return true
            }
        })
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus)
                searchView.clearFocus()
            else
                hideFilterPanel()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        if (item.itemId == R.id.menu_item_filter) {
            openFilterPanel()
            true
        } else super.onOptionsItemSelected(item)

    private fun performSearch() {
        binding.searchEmpty.isVisible = false
        hideFilterPanel()

        storeAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
        viewModel.newSearch()

        searchView.clearFocus()
    }

    private fun openFilterPanel() {
        searchView.clearFocus()
        binding.apply {
            searchSortingBackground.isVisible = true
            searchBottomSheetBehaviour.isVisible = true
        }
    }

    private fun hideFilterPanel(): Boolean {
        if (!binding.searchBottomSheetBehaviour.isVisible)
            return false
        binding.apply {
            searchSortingBackground.isVisible = false
            searchBottomSheetBehaviour.isVisible = false
        }
        return true
    }

    override fun onBackPressed(): Boolean {
        return hideFilterPanel()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
