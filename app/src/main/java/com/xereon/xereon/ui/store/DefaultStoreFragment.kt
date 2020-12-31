package com.xereon.xereon.ui.store

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.adapter.ProductLoadStateAdapter
import com.xereon.xereon.adapter.ProductPagingAdapter
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.databinding.FrgDefaultStoreBinding
import com.xereon.xereon.ui.MainActivity
import com.xereon.xereon.utils.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DefaultStoreFragment : Fragment(R.layout.frg_default_store) {
    companion object {
        const val CURRENT_STORE_ID = "current_store_id"
        const val CURRENT_STORE_NAME = "current_store_name"
    }
    private val viewModel: StoreViewModel by viewModels()

    private var _binding: FrgDefaultStoreBinding? = null
    private val binding get() = _binding!!

    private var storeId = 0;
    private lateinit var nameOfStore: String;

    private val adapter: ProductPagingAdapter = ProductPagingAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storeId = requireArguments().getInt(CURRENT_STORE_ID, 0)
        nameOfStore = requireArguments().getString(CURRENT_STORE_NAME, "")

        Log.d("[APP_DEBUG]", "DefaultStoreFragment: store is: $storeId ($nameOfStore)")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgDefaultStoreBinding.bind(view)
        (activity as MainActivity).setBottomNavBarVisibility(false)

        binding.storeProductsList.layoutManager = GridLayoutManager(context, 2)
        binding.storeProductsList.adapter = adapter.withLoadStateHeaderAndFooter(
            header = ProductLoadStateAdapter { adapter.retry() },
            footer = ProductLoadStateAdapter { adapter.retry() },
        )

        subscribeObserver()
        viewModel.setStoreId(storeId)
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

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    viewModel.searchProducts(query)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })
    }

    private fun subscribeObserver() {
        viewModel.storeData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Success<Store> -> {
                    binding.isLoading = false
                    binding.store = dataState.data
                }
                is DataState.Loading -> {
                    binding.isLoading = true
                }
                is DataState.Error -> {
                    displayError()
                    binding.isLoading = false
                    Log.e("[APP_DEBUG]", "observeStoreData() error: " + dataState.exception.message)
                }
            }
        })
        viewModel.productData.observe(viewLifecycleOwner, Observer {
            Log.d("[APP_DEBUG]", "subscribeObserver: got data successfully")
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
            binding.storeScrollParent.scrollTo(0, 800)
        })
    }

    private fun displayError(){
        val snackBar = Snackbar.make(requireView(), "Verbindungsfehler...", Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("Retry") {
            viewModel.getStoreData(true)
            viewModel.getAllProducts()
        }
        snackBar.setActionTextColor(resources.getColor(R.color.white))
        val snackBarView: View = snackBar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.error))
        snackBar.show()
    }
}