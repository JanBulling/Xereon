package com.xereon.xereon.ui.search

import android.os.Bundle
import android.util.Log.d
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.pagingAdapter.StoresPagingAdapter
import com.xereon.xereon.databinding.FrgSearchBinding
import com.xereon.xereon.util.Constants.TAG
import com.xereon.xereon.util.DataState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.frg_search) {
    private val viewModel by activityViewModels<SearchViewModel>()

    private var _binding: FrgSearchBinding? = null
    private val binding get() = _binding!!

    private val storeAdapter = StoresPagingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgSearchBinding.bind(view)

        binding.searchTestingRecycler.adapter = storeAdapter
        binding.searchTestingRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.noSearch = true
        binding.noResults = false

        subscribeToObserver()

        setHasOptionsMenu(true)
    }

    private fun subscribeToObserver() {
        viewModel.stores.observe(viewLifecycleOwner, Observer {
            storeAdapter.submitData(viewLifecycleOwner.lifecycle, it)
            binding.noSearch = false
        })
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
                super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Kategorie suchen"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true

            override fun onQueryTextChange(query: String?): Boolean {
                if (query.isNullOrEmpty())
                    return true
                else
                    viewModel.searchStoresByName(query)
                return true
            }
        })
    }
}