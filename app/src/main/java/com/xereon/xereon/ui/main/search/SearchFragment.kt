package com.xereon.xereon.ui.main.search

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.xereon.xereon.R
import com.xereon.xereon.data.category.Categories
import com.xereon.xereon.databinding.FragmentSearchBinding
import com.xereon.xereon.ui.base.StorePagingAdapter
import com.xereon.xereon.ui.main.MainActivity
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.lists.decorations.TopBottomPaddingDecorator
import com.xereon.xereon.util.ui.doNavigate
import com.xereon.xereon.util.ui.observeLiveData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.fragment_search) {
    private val viewModel by viewModels<SearchFragmentViewModel>()

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchAdapter = StorePagingAdapter()

    private lateinit var searchView: SearchView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<ConstraintLayout>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSearchBinding.bind(view)

        searchAdapter.addLoadStateListener {loadStates ->
            binding.apply {
                loading.isVisible = loadStates.source.refresh is LoadState.Loading    //initial load
                error.isVisible = loadStates.source.refresh is LoadState.Error &&
                        searchAdapter.itemCount == 0   //Error only shown if the view is empty

                //empty view
                noProducts.isVisible = loadStates.source.refresh is LoadState.NotLoading &&
                        loadStates.append.endOfPaginationReached &&
                        searchAdapter.itemCount == 0
            }
        }

        searchAdapter.setOnStoreClickListener { viewModel.onStoreClick(it.id, it.name) }

        binding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
            addItemDecoration(TopBottomPaddingDecorator(R.dimen.spacing_ultra_tiny))
            adapter = searchAdapter.withLoadStateHeaderAndFooter(
                header = SearchStateFooter{ searchAdapter.retry() },
                footer = SearchStateFooter{ searchAdapter.retry() },
            )
        }

        bottomSheetBehavior = BottomSheetBehavior.from(binding.sortBottomSheet)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback(){
            override fun onSlide(bottomSheet: View, slideOffset: Float) {/* NO-OP */}
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN)
                    binding.sortingBackground.isVisible = false
            }
        })
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        binding.apply {
            sortResultsOptions.setOnCheckedChangeListener { _, id ->
                val searchSorting = when (id) {
                    R.id.sort_results_new -> Constants.SortType.RESPONSE_NEW_FIRST
                    R.id.sort_results_old -> Constants.SortType.RESPONSE_OLD_FIRST
                    R.id.sort_results_A_Z -> Constants.SortType.RESPONSE_A_Z
                    R.id.sort_results_Z_A -> Constants.SortType.RESPONSE_Z_A
                    else -> Constants.SortType.RESPONSE_NEW_FIRST
                }
                viewModel.search(searchSorting)
            }
            applyFilters.setOnClickListener { performSearch() }
            sortingBackground.setOnClickListener { hideFilterPanel() }

            filterResultsSelect.setOnCheckedChangeListener { _, checkedId ->
                when(checkedId) {
                    View.NO_ID -> viewModel.search(-1)
                    R.id.filter_groceries -> viewModel.search(Categories.CATEGORY_GROCERIES.id)
                    R.id.filter_cloth -> viewModel.search(Categories.CATEGORY_CLOTH.id)
                    R.id.filter_services -> viewModel.search(Categories.CATEGORY_SERVICES.id)
                    R.id.filter_hotel -> viewModel.search(Categories.CATEGORY_HOTEL.id)
                    R.id.filter_restaurant -> viewModel.search(Categories.CATEGORY_RESTAURANT.id)
                    R.id.filter_entertainment -> viewModel.search(Categories.CATEGORY_ENTERTAINMENT.id)
                    R.id.filter_electronics -> viewModel.search(Categories.CATEGORY_ELECTRONIC.id)
                }
            }
        }

        viewModel.stores.observeLiveData(this) {
            searchAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            binding.loading.isVisible = false
            binding.empty.isVisible = false
        }

        viewModel.events.observeLiveData(this) {
            when (it) {
                is SearchEvents.OpenSortMenu -> showFilterPanel()
                is SearchEvents.NavigateToStore ->
                    doNavigate(
                        SearchFragmentDirections.actionSearchFragmentToStoreFragment(it.storeId, it.storeName)
                    )
            }
        }

        setupMenu()
    }

    private fun setupMenu() {
        binding.toolbar.apply {
            inflateMenu(R.menu.menu_search_fragment)
            setOnMenuItemClickListener {
                if (it.itemId == R.id.menu_item_filter) {
                    viewModel.onSortClick()
                    true
                } else false
            }
        }
        searchView = binding.toolbarSerch
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.search(newText ?: "")
                return true
            }
        })
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                searchView.clearFocus()
                (requireActivity() as MainActivity).setBottomNavBarVisibility(true)
            } else {
                hideFilterPanel()
                (requireActivity() as MainActivity).setBottomNavBarVisibility(false)
            }
        }
    }

    private fun performSearch() {
        binding.empty.isVisible = false
        searchAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
        binding.recyclerView.scrollToPosition(0)
        searchView.clearFocus()
        hideFilterPanel()
        viewModel.performSearch()
    }

    private fun showFilterPanel() {
        searchView.clearFocus()
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        binding.sortingBackground.isVisible = true
    }

    private fun hideFilterPanel() {
        binding.sortingBackground.isVisible = false
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}