package com.xereon.xereon.ui.store

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log.d
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.StringRes
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.adapter.loadStateAdapter.ProductsLoadStateAdapter
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.databinding.FrgDefaultStoreBinding
import com.xereon.xereon.ui._parent.MainActivity
import com.xereon.xereon.ui.product.DefaultProductFragmentDirections
import com.xereon.xereon.ui.store.ProductsPagingAdapter.Companion.VIEW_TYPE_PRODUCT
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DefaultStoreFragment : Fragment(R.layout.frg_default_store),
    ProductsPagingAdapter.ItemClickListener {
    private val viewModel by viewModels<StoreViewModel>()
    private val args by navArgs<DefaultStoreFragmentArgs>()

    private var _binding: FrgDefaultStoreBinding? = null
    private val binding get() = _binding!!

    private val productsAdapter =
        ProductsPagingAdapter()

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
        (activity as MainActivity).setActionBarTitle(storeName)

        val gridLayoutManager = GridLayoutManager(context, 2)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                val viewType = productsAdapter.getItemViewType(position)
                return if (viewType == VIEW_TYPE_PRODUCT) 1 else 2
            }
        }
        productsAdapter.setOnItemClickListener(this)

        binding.apply {
            storeRecycler.apply {
                layoutManager = gridLayoutManager
                setHasFixedSize(true)
                adapter = productsAdapter.withCustomLoadStateFooter(
                    footer = ProductsLoadStateAdapter { productsAdapter.retry() }
                )
            }
            storeNotFoundRetry.setOnClickListener {
                viewModel.getStore(storeID)
                viewModel.getAllProducts(storeID)
            }
        }

        subscribeObserver()

        viewModel.getStore(storeID)
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
                searchView.setQuery(viewModel.queryText, false)
            else if (searchView.query.isNullOrBlank() && !viewModel.queryText.isBlank()) {
                productsAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                binding.storeRecycler.scrollToPosition(1)
                viewModel.searchProduct("")
                searchView.clearFocus()
            }
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                productsAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                binding.storeRecycler.scrollToPosition(1)
                viewModel.searchProduct(query ?: "")
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_item_new_first -> sortProducts(Constants.SortType.RESPONSE_NEW_FIRST)
            R.id.menu_item_old_first -> sortProducts(Constants.SortType.RESPONSE_OLD_FIRST)
            R.id.menu_item_order_price_low -> sortProducts(Constants.SortType.RESPONSE_PRICE_LOW)
            R.id.menu_item_order_price_high -> sortProducts(Constants.SortType.RESPONSE_PRICE_HIGH)
            R.id.menu_item_order_app_offer -> sortProducts(Constants.SortType.RESPONSE_ONLY_IN_APP)
            R.id.menu_item_order_a_z -> sortProducts(Constants.SortType.RESPONSE_A_Z)
            R.id.menu_item_order_z_a -> sortProducts(Constants.SortType.RESPONSE_Z_A)
            else ->
                super.onOptionsItemSelected(item)
        }
    }

    private fun sortProducts(sorting: Constants.SortType): Boolean {
        productsAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
        binding.storeRecycler.scrollToPosition(1)
        viewModel.sortProduct(sorting)
        return true
    }

    private fun subscribeObserver() {
        viewModel.storeData.observe(viewLifecycleOwner, Observer { event ->
            when (event) {
                is StoreViewModel.StoreEvent.Success -> {
                    (activity as MainActivity).setActionBarTitle(event.storeData.name)
                    productsAdapter.setStore(event.storeData)
                    binding.isSuccessful = true
                    binding.isLoading = false
                }
                is StoreViewModel.StoreEvent.Failure -> {
                    binding.isLoading = false
                    binding.isSuccessful = false
                }
                is StoreViewModel.StoreEvent.Loading -> {
                    binding.isLoading = true
                }
            }
        })
        viewModel.products.observe(viewLifecycleOwner, Observer {
            productsAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect { event ->
                when (event) {
                    is StoreViewModel.StoreEvent.ShowErrorMessage -> displayError(event.messageId)
                    is StoreViewModel.StoreEvent.ShowAddedFavorites -> displayFavorite()
                    else -> Unit
                }
            }
        }
    }

    override fun onItemClick(simpleProduct: SimpleProduct) {
        val action = DefaultProductFragmentDirections.actionToProduct(simpleProduct)
        findNavController().navigate(action)
    }

    override fun onSearchClicked() {
        d(TAG, "search")
    }

    override fun onAddToFavoriteClicked() {
        viewModel.addStoreToFavorites()
    }

    override fun onNavigationClicked(latitude: LatLng) {
        val navigationUri = Uri.parse("geo:${latitude.latitude},${latitude.longitude}")
        val intent = Intent(Intent.ACTION_VIEW, navigationUri)
        if (intent.resolveActivity(requireActivity().packageManager) != null)
            startActivity(intent)
        else
            Snackbar.make(requireView(), "Navigieren ist auf diesem Gerät nicht möglich", Snackbar.LENGTH_LONG).show()
    }

    private fun displayError(@StringRes messageId: Int) {
        val snackBar = Snackbar.make(requireView(), messageId, Snackbar.LENGTH_SHORT)
        val snackBarView: View = snackBar.view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            snackBarView.setBackgroundColor(resources.getColor(R.color.error, null))
        snackBar.show()
    }

    private fun displayFavorite() {
        Snackbar.make(requireView(), "Filiale wurde als Favorit gespeichert", Snackbar.LENGTH_LONG)
            .setAction("Zu den Favoriten") {
                findNavController().navigate(R.id.action_Store_to_Favorites)
            }.setActionTextColor(Color.parseColor("#ADD8E6"))
            .show()
    }
}