package com.xereon.xereon.ui.categories

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.loadStateAdapter.StoresLoadStateAdapter
import com.xereon.xereon.ui.MainActivity
import com.xereon.xereon.adapter.pagingAdapter.StoresPagingAdapter
import com.xereon.xereon.data.store.SimpleStore
import com.xereon.xereon.databinding.FrgSubCategoryBinding
import com.xereon.xereon.ui.store.DefaultStoreFragmentDirections
import com.xereon.xereon.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubCategoryFragment : Fragment(R.layout.frg_sub_category) {
    private val viewModel by viewModels<CategoryViewModel>()
    private val args by navArgs<SubCategoryFragmentArgs>()

    private var _binding: FrgSubCategoryBinding? = null
    private val binding get() = _binding!!

    private var storeAdapter = StoresPagingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgSubCategoryBinding.bind(view)
        (activity as MainActivity).setActionBarTitle(args.type)

        storeAdapter.setOnItemClickListener(object: StoresPagingAdapter.ItemClickListener{
            override fun onItemClick(simpleStore: SimpleStore) {
                val action = DefaultStoreFragmentDirections.actionToStore(simpleStore = simpleStore)
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
        binding.apply {
            buttonRetry.setOnClickListener {
                storeAdapter.retry()
            }
            subCategoryStores.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                itemAnimator = null
                adapter = storeAdapter.withLoadStateFooter(
                    footer = StoresLoadStateAdapter { storeAdapter.retry() }
                )
            }
        }

        subscribeToObserver()

        viewModel.searchStore(CategoryViewModel.SearchQuery(type = args.type, postcode = Constants.DEFAULT_POSTCODE))

        setHasOptionsMenu(true)
    }

    private fun subscribeToObserver() {
        viewModel.typeStores.observe(viewLifecycleOwner, Observer {
            storeAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.menu_item_search_product)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Kategorie suchen"
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                searchView.setQuery(viewModel.searchQuery, false)
            else if (searchView.query.isNullOrBlank() && !viewModel.searchQuery.isBlank()) {
                storeAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())

                binding.subCategoryStores.scrollToPosition(0)
                viewModel.searchStore(CategoryViewModel.SearchQuery(type = args.type, postcode = Constants.DEFAULT_POSTCODE))
                searchView.clearFocus()
            }
        }
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                storeAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())

                binding.subCategoryStores.scrollToPosition(0)
                viewModel.searchStore(CategoryViewModel.SearchQuery(query ?: "", args.type, Constants.DEFAULT_POSTCODE))
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(query: String?) = true
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}