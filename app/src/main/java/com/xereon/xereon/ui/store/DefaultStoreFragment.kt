package com.xereon.xereon.ui.store

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import com.xereon.xereon.data.products.SimpleProduct
import com.xereon.xereon.databinding.FrgDefaultStoreBinding
import com.xereon.xereon.ui.MainActivity
import com.xereon.xereon.ui.product.DefaultProductFragmentDirections
import com.xereon.xereon.ui.store.ProductsPagingAdapter.Companion.VIEW_TYPE_PRODUCT
import com.xereon.xereon.util.Constants
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

        inflater.inflate(R.menu.menu_store_products, menu)

        val searchItem = menu.findItem(R.id.menu_item_search_product)
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
            R.id.menu_item_sort_new_first -> sortProducts(Constants.SortType.RESPONSE_NEW_FIRST)
            R.id.menu_item_sort_old_first -> sortProducts(Constants.SortType.RESPONSE_OLD_FIRST)
            R.id.menu_item_sort_price_low -> sortProducts(Constants.SortType.RESPONSE_PRICE_LOW)
            R.id.menu_item_sort_price_high -> sortProducts(Constants.SortType.RESPONSE_PRICE_HIGH)
            R.id.menu_item_sort_app_offer -> sortProducts(Constants.SortType.RESPONSE_ONLY_IN_APP)
            R.id.menu_item_sort_a_z -> sortProducts(Constants.SortType.RESPONSE_A_Z)
            R.id.menu_item_sort_z_a -> sortProducts(Constants.SortType.RESPONSE_Z_A)
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
                    val store = event.storeData
                    (activity as MainActivity).setActionBarTitle(store.name)
                    storeName = store.name
                    storeID = store.id

                    /*store.placesData = GooglePlacesData(
                        rating = 4.1f,
                        numberRating = 379,
                        currentPopularity = 55,
                        popularTimes = listOf(
                            arrayOf(0,0,0,0,0,0, 20, 32, 45, 65, 70, 60, 50, 50, 65, 80, 82, 62, 40, 15,  5, 0,0,0),
                            arrayOf(0,0,0,0,0,0, 15, 28, 40, 60, 65, 65, 62, 68, 72, 75, 68, 40, 30, 15,  8, 0,0,0),
                            arrayOf(0,0,0,0,0,0, 15, 28, 40, 55, 62, 62, 65, 72, 78, 82, 65, 55, 30, 18,  8, 0,0,0),
                            arrayOf(0,0,0,0,0,0, 12, 24, 55, 72, 85, 77, 72, 68, 72, 77, 72, 55, 30, 12,  8, 0,0,0),
                            arrayOf(0,0,0,0,0,0, 20, 35, 52, 62, 68, 65, 62, 65, 72, 78, 75, 58, 45, 19, 11, 0,0,0),
                            arrayOf(0,0,0,0,0,0, 15, 32, 57, 80, 95, 98, 95, 90, 93, 98, 95, 78, 52, 26, 12, 0,0,0),
                            arrayOf(0,0,0,0,0,0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 0,0,0)
                        )
                    )*/
                    productsAdapter.setStore(store)
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

    override fun onChatClicked() {
        val action = DefaultStoreFragmentDirections.actionToChat(storeId = storeID, storeName = storeName)
        findNavController().navigate(action)
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