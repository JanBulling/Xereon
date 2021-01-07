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
import com.xereon.xereon.databinding.FrgExploreBinding
import com.xereon.xereon.databinding.FrgSearchBinding
import com.xereon.xereon.util.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frg_search.*

@AndroidEntryPoint
class SearchFragment : Fragment(R.layout.frg_search) {
    private val viewModel by activityViewModels<SearchViewModel>()

    private var _binding: FrgSearchBinding? = null
    private val binding get() = _binding!!

    private lateinit var storeAdapter: StoresPagingAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgSearchBinding.bind(view)
        setHasOptionsMenu(true)

        storeAdapter = StoresPagingAdapter()

        binding.searchTestingRecycler.adapter = storeAdapter
        binding.searchTestingRecycler.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
        }

        binding.noSearch = true
        binding.noResults = false

        subscribeToObserver()
    }

    private fun subscribeToObserver() {
        viewModel.stores.observe(viewLifecycleOwner, Observer {
            d(TAG, "received stores")
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

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null)
                    viewModel.searchStoresByName(query)

                return true
            }

            override fun onQueryTextChange(newText: String?) = true

        })
    }
}