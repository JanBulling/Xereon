package com.xereon.xereon.ui.stores.products

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.loadStateAdapter.ProductsLoadStateAdapter
import com.xereon.xereon.databinding.FragmentStoreProductsBinding
import com.xereon.xereon.ui.main.MainActivity
import com.xereon.xereon.ui.stores.StoreEvents
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.lists.decorations.LeftRightPaddingDecorator
import com.xereon.xereon.util.ui.doNavigate
import com.xereon.xereon.util.ui.observeLiveData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreProductsFragment : Fragment(R.layout.fragment_store_products) {
    private val viewModel by viewModels<StoreProductsFragmentViewModel>()
    private val args by navArgs<StoreProductsFragmentArgs>()

    private var _binding: FragmentStoreProductsBinding? = null
    private val binding get() = _binding!!

    private val productsAdapter = StoreProductsAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStoreProductsBinding.bind(view)

        productsAdapter.addLoadStateListener { loadStates ->
            binding.apply {
                loading.isVisible = loadStates.source.refresh is LoadState.Loading    //initial load
                error.isVisible = loadStates.source.refresh is LoadState.Error &&
                        productsAdapter.itemCount == 0   //Error only shown if the view is empty

                //empty view
                noProducts.isVisible = loadStates.source.refresh is LoadState.NotLoading &&
                        loadStates.append.endOfPaginationReached &&
                        productsAdapter.itemCount == 0
            }
        }

        productsAdapter.setOnProductClickListener { viewModel.onProductClick(it.id, it.name) }

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = productsAdapter.getItemViewType(position)
                return if (viewType == StoreProductsAdapter.VIEW_TYPE_PRODUCT) 1 else 2
            }
        }
        binding.recyclerView.apply {
            layoutManager = gridLayoutManager
            itemAnimator = null
            addItemDecoration(LeftRightPaddingDecorator(startPadding = R.dimen.spacing_ultra_tiny))
            adapter = productsAdapter.withLoadStateHeaderAndFooter(
                header = StoreProductsStateFooter { productsAdapter.retry() },
                footer = StoreProductsStateFooter { productsAdapter.retry() }
            )
            setHasFixedSize(true)
        }


        viewModel.products.observeLiveData(this) {
            productsAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            binding.loading.isVisible = false
        }

        viewModel.events.observeLiveData(this) {
            when (it) {
                is StoreEvents.NavigateBack -> (requireActivity() as MainActivity).goBack()
                is StoreEvents.NavigateToProduct -> {
                    doNavigate(
                        StoreProductsFragmentDirections.actionStoreProductsFragmentToProductFragment(
                            it.productId,
                            it.productName
                        )
                    )
                }
            }
        }

        setupMenu()
        viewModel.getAllProducts(args.storeId)
    }

    private fun setupMenu() {
        binding.toolbar.apply {
            inflateMenu(R.menu.menu_store_products)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_item_sort_new_first -> sortProducts(Constants.SortType.RESPONSE_NEW_FIRST)
                    R.id.menu_item_sort_old_first -> sortProducts(Constants.SortType.RESPONSE_OLD_FIRST)
                    R.id.menu_item_sort_price_high -> sortProducts(Constants.SortType.RESPONSE_PRICE_HIGH)
                    R.id.menu_item_sort_price_low -> sortProducts(Constants.SortType.RESPONSE_PRICE_LOW)
                    R.id.menu_item_sort_a_z -> sortProducts(Constants.SortType.RESPONSE_A_Z)
                    R.id.menu_item_sort_z_a -> sortProducts(Constants.SortType.RESPONSE_Z_A)
                    R.id.menu_item_sort_app_offer -> sortProducts(Constants.SortType.RESPONSE_ONLY_IN_APP)
                    else -> super.onOptionsItemSelected(it)
                }
            }

            val searchView = menu.findItem(R.id.menu_item_search_product).actionView as SearchView
            searchView.queryHint = "Produkt suchen"
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    productsAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.searchProducts(query ?: "")
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(query: String?) = true
            })
            searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus)
                    searchView.setQuery(viewModel.search.query, false)
                if (!hasFocus && searchView.query.isNullOrBlank() && viewModel.search.query.isNotBlank()) {
                    productsAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.searchProducts("")
                    searchView.clearFocus()
                }
            }

            setNavigationOnClickListener { viewModel.onBackClick() }
            title = args.storeName
        }
    }

    private fun sortProducts(sorting: Constants.SortType): Boolean {
        productsAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
        binding.recyclerView.scrollToPosition(0)
        viewModel.orderProducts(sorting)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}