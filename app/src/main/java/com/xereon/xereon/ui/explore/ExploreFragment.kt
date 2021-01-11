package com.xereon.xereon.ui.explore

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.adapter.recyclerAdapter.ProductHorizontalAdapter
import com.xereon.xereon.adapter.recyclerAdapter.StoreHorizontalAdapter
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.data.util.CategoryUtils
import com.xereon.xereon.databinding.FrgExploreBinding
import com.xereon.xereon.ui._parent.MainActivity
import com.xereon.xereon.ui.categories.CategoryFragmentDirections
import com.xereon.xereon.ui.product.DefaultProductFragmentDirections
import com.xereon.xereon.ui.store.DefaultStoreFragmentDirections
import com.xereon.xereon.util.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frg_explore.*

@AndroidEntryPoint
class ExploreFragment : Fragment(R.layout.frg_explore), ProductHorizontalAdapter.ItemClickListener, OnTableItemSelect {
    private val viewModel by activityViewModels<ExploreViewModel>()

    private var _binding: FrgExploreBinding? = null
    private val binding get() = _binding!!

    private val newStoresAdapter = StoreHorizontalAdapter()

    private val recommendationsAdapter = ProductHorizontalAdapter()
    private val popularAdapter = ProductHorizontalAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgExploreBinding.bind(view)
        (activity as MainActivity).setBottomNavBarVisibility(true)

        recommendationsAdapter.setOnItemClickListener(this)
        popularAdapter.setOnItemClickListener(this)
        newStoresAdapter.setOnItemClickListener(object: StoreHorizontalAdapter.ItemClickListener{
            override fun onItemClick(simpleStore: SimpleStore) {
                val action = DefaultStoreFragmentDirections.actionToStore(simpleStore)
                findNavController().navigate(action)
            }
        })

        binding.locationCity = "Herbrechtingen"
        binding.exploreRecyclerNewStores.apply {
            setHasFixedSize(true)
            adapter = newStoresAdapter
        }
        binding.exploreRecyclerPopular.apply{
            setHasFixedSize(true)
            adapter = popularAdapter
        }
        binding.exploreRecyclerRecommendations.apply {
            setHasFixedSize(true)
            adapter = recommendationsAdapter
        }
        binding.tableCategoryClickListener = this

        explore_all_categories.setOnClickListener { _ ->
            findNavController().navigate(R.id.action_To_AllCategories)
        }

        subscribeObserver()

        if (savedInstanceState == null)
            viewModel.getExploreData(1, "89542")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeObserver() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is DataState.Success<ExploreData> -> {
                    binding.isLoading = false
                    newStoresAdapter.submitList(dataState.data.newStores)
                    recommendationsAdapter.submitList(dataState.data.recommendations)
                    popularAdapter.submitList(dataState.data.popular)
                }
                is DataState.Loading -> {
                    binding.isLoading = true
                }
                is DataState.Error -> {
                    displayError(dataState.message)
                    binding.isLoading = false
                }
            }
        })
    }

    private fun displayError(message: String) {
        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("Retry") {
            viewModel.getExploreData(1, "89542", true)
        }
        snackBar.setActionTextColor(Color.WHITE)
        val snackBarView: View = snackBar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.error))
        snackBar.show()
    }

    override fun onItemClick(simpleProduct: SimpleProduct) {
        val action = DefaultProductFragmentDirections.actionToProduct(simpleProduct)
        findNavController().navigate(action)
    }

    override fun onTableSelect(index: Int) {
        val category = CategoryUtils.getCategory(index)
        val action = CategoryFragmentDirections.actionToCategory(category)
        findNavController().navigate(action)
    }
}