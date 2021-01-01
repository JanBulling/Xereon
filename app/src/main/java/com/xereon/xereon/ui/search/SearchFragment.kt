package com.xereon.xereon.ui.search

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.ProductLoadStateAdapter
import com.xereon.xereon.adapter.ProductPagingAdapter
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.databinding.FrgDefaultStoreBinding
import com.xereon.xereon.databinding.FrgSearchBinding
import com.xereon.xereon.ui.MainActivity
import com.xereon.xereon.ui.store.StoreViewModel
import com.xereon.xereon.utils.DataState

class SearchFragment : Fragment(R.layout.frg_search) {
    private val viewModel: SearchViewModel by activityViewModels()

    private var _binding: FrgSearchBinding? = null
    private val binding get() = _binding!!

    private val adapter: ProductPagingAdapter = ProductPagingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgSearchBinding.bind(view)

        binding.searchTestingRecycler.layoutManager = GridLayoutManager(context, 2)
        binding.searchTestingRecycler.adapter = adapter.withLoadStateHeaderAndFooter(
            header = ProductLoadStateAdapter { adapter.retry() },
            footer = ProductLoadStateAdapter { adapter.retry() },
        )

        subscribeObserver()

        viewModel.searchProducts("")

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null)
                    viewModel.searchProducts(query)
                else
                    viewModel.searchProducts("")
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeObserver() {
        viewModel.productData.observe(viewLifecycleOwner, Observer {
            Log.d("[APP_DEBUG]", "subscribeObserver: got data successfully")
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
            binding.searchTestingRecycler.scrollToPosition(0)
        })
    }

}