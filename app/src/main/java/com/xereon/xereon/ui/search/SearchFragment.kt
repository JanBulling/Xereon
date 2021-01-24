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

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<*>;

    private val storeAdapter = StoresPagingAdapter()
    private lateinit var searchView: SearchView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgSearchBinding.bind(view)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.searchBottomSheetBehaviour)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.peekHeight = 850
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        bottomSheetBehavior.setBottomSheetCallback(object: BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(p0: View, p1: Float) { /*NO-OP*/ }
            override fun onStateChanged(p0: View, state: Int) {
                binding.isSorting = state != BottomSheetBehavior.STATE_HIDDEN
            }
        })

        binding.alreadySearched = false

        storeAdapter.setOnItemClickListener(object: StoresPagingAdapter.ItemClickListener {
            override fun onItemClick(simpleStore: SimpleStore) {
                val action = DefaultStoreFragmentDirections.actionToStore(simpleStore)
                findNavController().navigate(action)
            }
        })
        storeAdapter.addLoadStateListener { loadStates ->
            binding.apply{
                searchLoading.isVisible = loadStates.source.refresh is LoadState.Loading    //initial load
                isError = loadStates.source.refresh is LoadState.Error    //

                //empty view
                noResults = loadStates.source.refresh is LoadState.NotLoading &&
                        loadStates.append.endOfPaginationReached &&
                        storeAdapter.itemCount < 1
            }
        }
        binding.buttonRetry.setOnClickListener{
            storeAdapter.retry()
        }

        binding.searchRecycler.apply {
            setHasFixedSize(true)
            itemAnimator = null
            adapter = storeAdapter.withLoadStateFooter(
                    footer = StoresLoadStateAdapter { storeAdapter.retry() }
            )
        }

        val categoriesChipGroup = binding.searchChipGroupSelectFilter
        for (i in 0 until (CategoryUtils.getNumberOfCategories()-1)) {
            val chip = Chip(requireContext())
            chip.text = CategoryUtils.getCategoryName(i)
            chip.isCheckable = true

            categoriesChipGroup.addView(chip)
            chip.setOnClickListener {
                if (chip.isChecked) //gets checked or unchecked first, so test for opposit
                    viewModel.category = i
            }

            if (i == viewModel.category)    //check button by default
                categoriesChipGroup.check(chip.id)
        }
        categoriesChipGroup.setOnCheckedChangeListener { _, id ->
            if (id == View.NO_ID)
                viewModel.category = -1
        }

        binding.searchSearchBtn.setOnClickListener{
            performSearch()
        }

        binding.searchSortRadioGroup.setOnCheckedChangeListener { _, id ->
            val searchSorting =  when (id) {
                R.id.search_sort_default -> Constants.SortTypes.SORT_RESPONSE_DEFAULT
                R.id.search_sort_A_Z -> Constants.SortTypes.SORT_RESPONSE_A_Z
                R.id.search_sort_Z_A -> Constants.SortTypes.SORT_RESPONSE_Z_A
                else -> Constants.SortTypes.SORT_RESPONSE_DEFAULT
            }

            viewModel.sorting = searchSorting
        }

        /*binding.searchExpandRegion.isChecked = viewModel.expandedRegion
        binding.searchExpandRegion.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onExpandedSettingChanged(isChecked)
        }*/

        binding.searchFab.setOnClickListener { openFilterPanel() }
        binding.searchSortingBackground.setOnClickListener{ hideFilterPanel() }

        binding.searchInfoExpandRegion.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Suchregion erweitern")
                .setMessage("Falls in Ihrer nahen Umgebung keine passenden Filialen gefunden wurden, sucht Xereon automatisch nach weiter entfernten Treffern")
                .setIcon(R.drawable.ic_location)
                .setNegativeButton("Okay", null)
                .show()
        }

        subscribeToObserver()

        setHasOptionsMenu(true)
    }

    private fun subscribeToObserver() {
        viewModel.searchResponse.observe(viewLifecycleOwner, Observer {
            storeAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            binding.alreadySearched = true
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_search_fragment, menu)

        searchView = (requireActivity() as MainActivity).getSearch()
        searchView.queryHint = "Filiale suchen"

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) : Boolean {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_item_filter) {
            openFilterPanel()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun performSearch() {
        binding.alreadySearched = true
        hideFilterPanel()

        storeAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
        viewModel.newSearch()

        searchView.clearFocus()
    }

    private fun openFilterPanel() {
        searchView.clearFocus()
        binding.isSorting = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    private fun hideFilterPanel() : Boolean {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN)
            return false

        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.isSorting = false
        return true
    }

    override fun onBackPressed(): Boolean {
        return hideFilterPanel()
    }
}
