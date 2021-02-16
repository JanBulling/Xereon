package com.xereon.xereon.ui.main.category.subCategories

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.databinding.FragmentSubCategoryBinding
import com.xereon.xereon.ui.base.StorePagingAdapter
import com.xereon.xereon.ui.main.MainActivity
import com.xereon.xereon.util.lists.decorations.TopBottomPaddingDecorator
import com.xereon.xereon.util.ui.doNavigate
import com.xereon.xereon.util.ui.observeLiveData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubCategoryFragment : Fragment(R.layout.fragment_sub_category) {
    private val viewModel by viewModels<SubCategoryFragmentViewModel>()
    private val args by navArgs<SubCategoryFragmentArgs>()

    private var _binding : FragmentSubCategoryBinding? = null
    private val binding get() = _binding!!

    private val storeAdapter = StorePagingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentSubCategoryBinding.bind(view)

        storeAdapter.addLoadStateListener { loadStates ->
            binding.apply {
                loading.isVisible = loadStates.source.refresh is LoadState.Loading    //initial load
                error.isVisible = loadStates.source.refresh is LoadState.Error &&
                        storeAdapter.itemCount == 0   //Error only shown if the view is empty

                //empty view
                noStores.isVisible = loadStates.source.refresh is LoadState.NotLoading &&
                        loadStates.append.endOfPaginationReached &&
                        storeAdapter.itemCount == 0
            }
        }

        storeAdapter.setOnStoreClickListener {
            doNavigate(SubCategoryFragmentDirections.actionSubCategoryFragment2ToStoreFragment(it.id, it.name))
        }

        binding.recyclerView.apply {
            itemAnimator = null
            addItemDecoration(TopBottomPaddingDecorator(R.dimen.spacing_ultra_tiny))
            adapter = storeAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewModel.events.observeLiveData(this) {
            when (it) {
                is SubCategoryEvents.NavigateBack ->
                    (requireActivity() as MainActivity).goBack()
            }
        }

        viewModel.stores.observeLiveData(this) {
            binding.loading.isVisible = false
            storeAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        setupMenu()

        viewModel.getStores(args.subCategory)
    }

    private fun setupMenu() {
        binding.toolbar.apply {
            inflateMenu(R.menu.menu_sub_category)
            title = args.subCategory
            setNavigationOnClickListener {
                viewModel.onBackClick()
            }

            val searchView = menu.findItem(R.id.menu_item_search_store).actionView as SearchView
            searchView.queryHint = "Filiale suchen"
            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    storeAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.searchStore(query ?: "")
                    searchView.clearFocus()
                    return true
                }

                override fun onQueryTextChange(query: String?) = true
            })
            searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
                if (hasFocus)
                    searchView.setQuery(viewModel.query.query, false)
                if (!hasFocus && searchView.query.isNullOrBlank() && viewModel.query.query.isNotBlank()) {
                    storeAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.searchStore("")
                    searchView.clearFocus()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}