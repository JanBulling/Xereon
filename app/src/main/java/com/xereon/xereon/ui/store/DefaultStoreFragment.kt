package com.xereon.xereon.ui.store

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.adapter.ProductLoadStateAdapter
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.databinding.FrgDefaultStoreBinding
import com.xereon.xereon.ui.MainActivity
import com.xereon.xereon.ui.product.DefaultProductFragmentDirections
import com.xereon.xereon.ui.store.StorePagingAdapter.Companion.VIEW_TYPE_PRODUCT
import com.xereon.xereon.util.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DefaultStoreFragment : Fragment(R.layout.frg_default_store), StorePagingAdapter.OnClickListener {
    private val viewModel by viewModels<StoreViewModel>()
    private val args by navArgs<DefaultStoreFragmentArgs>()

    private var _binding: FrgDefaultStoreBinding? = null
    private val binding get() = _binding!!

    private val adapter: StorePagingAdapter = StorePagingAdapter(this)

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
                val viewType = adapter.getItemViewType(position)

                return if (viewType == VIEW_TYPE_PRODUCT) 1 else 2
            }
        }
        binding.storeRecycler.layoutManager = gridLayoutManager
        binding.storeRecycler.setHasFixedSize(true)
        binding.storeRecycler.adapter = adapter.withCustomLoadStateFooter(
            footer = ProductLoadStateAdapter { adapter.retry() }
        )
        binding.storeNotFoundRetry.setOnClickListener {
            viewModel.getStoreData(true)
            viewModel.getAllProducts()
        }

        subscribeObserver()
        viewModel.setStoreId(storeID)
        viewModel.setUserId(1)

        viewModel.getStoreData()
        viewModel.getAllProducts()

        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Produkte suchen"
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                searchView.setQuery(viewModel.currentQuery.value, false)
            else if (searchView.query.isNullOrBlank() && !viewModel.currentQuery.value.isNullOrBlank()) {
                    adapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                    binding.storeRecycler.scrollToPosition(1)
                    viewModel.searchProducts("")
                    searchView.clearFocus()
            }
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                binding.storeRecycler.scrollToPosition(1)
                viewModel.searchProducts(query ?: "")
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })
    }

    private fun subscribeObserver() {
        viewModel.storeData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Success<Store> -> {
                    (activity as MainActivity).setActionBarTitle(dataState.data.name)
                    adapter.setStore(dataState.data)
                    binding.isSuccessful = true
                    binding.isLoading = false
                }
                is DataState.Loading -> {
                    binding.isLoading = true
                }
                is DataState.Error -> {
                    //displayError(dataState.message)
                    binding.isSuccessful = false
                    binding.isLoading = false
                }
            }
        })
        viewModel.productData.observe(viewLifecycleOwner, Observer {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    private fun displayError(string: String){
        val snackBar = Snackbar.make(requireView(), string, Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("Retry") {
            viewModel.getStoreData(true)
            viewModel.getAllProducts()
        }
        snackBar.setActionTextColor(resources.getColor(R.color.white))
        val snackBarView: View = snackBar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.error))
        snackBar.show()
    }

    override fun onClick(product: SimpleProduct) {
        val action = DefaultProductFragmentDirections.actionToProduct(product)
        findNavController().navigate(action)

        //Toast.makeText(context, "Clicked on: ${product.name}", Toast.LENGTH_SHORT).show()
    }
}