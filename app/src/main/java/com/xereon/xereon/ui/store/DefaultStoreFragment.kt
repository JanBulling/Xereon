package com.xereon.xereon.ui.store

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.loadStateAdapter.ProductsLoadStateAdapter
import com.xereon.xereon.adapter.pagingAdapter.ProductsPagingAdapter
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.databinding.FrgDefaultStoreBinding
import com.xereon.xereon.ui._parent.MainActivity
import com.xereon.xereon.ui.product.DefaultProductFragmentDirections
import com.xereon.xereon.adapter.pagingAdapter.ProductsPagingAdapter.Companion.VIEW_TYPE_PRODUCT
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DefaultStoreFragment : Fragment(R.layout.frg_default_store), ProductsPagingAdapter.ItemClickListener {
    private val viewModel by viewModels<StoreViewModel>()
    private val args by navArgs<DefaultStoreFragmentArgs>()

    private var _binding: FrgDefaultStoreBinding? = null
    private val binding get() = _binding!!

    private val productsAdapter = ProductsPagingAdapter()

    private var storeID: Int = -1
    private var storeName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (args.simpleStore == null)
            storeID = args.simpleStoreId
        else {
            storeID = args.simpleStore!!.id
            storeName = args.simpleStore!!.name
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgDefaultStoreBinding.bind(view)
        (activity as MainActivity).setBottomNavBarVisibility(false)
        (activity as MainActivity).setActionBarTitle(storeName)

        val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.spanSizeLookup = object: GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = productsAdapter.getItemViewType(position)
                return if (viewType == VIEW_TYPE_PRODUCT) 1 else 2
            }
        }

        productsAdapter.setOnItemClickListener(this)

        binding.storeRecycler.apply {
            layoutManager = gridLayoutManager
            setHasFixedSize(true)
            adapter = productsAdapter.withCustomLoadStateFooter(
                footer = ProductsLoadStateAdapter { productsAdapter.retry() }
            )
        }

        binding.storeNotFoundRetry.setOnClickListener {
            viewModel.getStoreData(storeID, true)
            viewModel.getAllProducts(storeID)
        }

        subscribeObserver()

        viewModel.getStoreData(storeID)
        viewModel.getAllProducts(storeID)

        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_store, menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Produkte suchen"
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                searchView.setQuery(viewModel.currentQuery, false)
            else if (searchView.query.isNullOrBlank() && !viewModel.currentQuery.isBlank()) {
                productsAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                binding.storeRecycler.scrollToPosition(1)
                viewModel.searchProducts("")
                searchView.clearFocus()
            }
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                productsAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                binding.storeRecycler.scrollToPosition(1)
                viewModel.searchProducts(query ?: "")
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_order_default -> orderProducts(Constants.ORDER_DEFAULT)
            R.id.menu_item_order_price_low -> orderProducts(Constants.ORDER_PRICE_LOW)
            R.id.menu_item_order_price_high -> orderProducts(Constants.ORDER_PRICE_HIGH)
            R.id.menu_item_order_app_offer -> orderProducts(Constants.ORDER_ONLY_IN_APP)
            R.id.menu_item_order_a_z -> orderProducts(Constants.ORDER_NAME_A_Z)
            R.id.menu_item_order_z_a -> orderProducts(Constants.ORDER_NAME_Z_A)
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    private fun orderProducts(orderIndex: Int): Boolean {
        if (viewModel.currentProductOrder != orderIndex) {
            productsAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
            binding.storeRecycler.scrollToPosition(1)
            viewModel.sortProducts(orderIndex)
            return true
        }
        return false
    }

    private fun subscribeObserver() {
        viewModel.storeData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Success<Store> -> {
                    (activity as MainActivity).setActionBarTitle(dataState.data.name)
                    productsAdapter.setStore(dataState.data)
                    binding.isSuccessful = true
                    binding.isLoading = false
                }
                is DataState.Loading -> {
                    binding.isLoading = true
                }
                is DataState.Error -> {
                    binding.isSuccessful = false
                    binding.isLoading = false
                }
            }
        })
        viewModel.productData.observe(viewLifecycleOwner, Observer {
            productsAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    override fun onItemClick(simpleProduct: SimpleProduct) {
        val action = DefaultProductFragmentDirections.actionToProduct(simpleProduct)
        findNavController().navigate(action)
    }
}