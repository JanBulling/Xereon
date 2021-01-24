package com.xereon.xereon.ui.explore

import android.graphics.Color
import android.os.Bundle
import android.util.Log.d
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frg_explore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest

@ExperimentalCoroutinesApi
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

        setHasOptionsMenu(true)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_explore_fragment, menu)

        val actionView = menu.findItem(R.id.menu_item_shopping_cart).actionView
        val textView = actionView.findViewById<TextView>(R.id.cart_badge)

        viewModel.ordersCount.observe(viewLifecycleOwner, Observer { numberOrder ->
            val text =  if (numberOrder >= 100)
                    "99"
                else
                    "$numberOrder"
            textView.text = text
        })

        actionView.setOnClickListener {
            findNavController().navigate(R.id.action_to_ShoppingCart)
        }
    }

    private fun subscribeObserver() {
        viewModel.exploreData.observe(viewLifecycleOwner, Observer {event ->
            when (event) {
                is ExploreViewModel.ExploreEvent.Success -> {
                    binding.isLoading = false
                    newStoresAdapter.submitList(event.exploreData.newStores)
                    recommendationsAdapter.submitList(event.exploreData.recommendations)
                    popularAdapter.submitList(event.exploreData.popular)
                }
                is ExploreViewModel.ExploreEvent.Failure -> {
                    binding.isLoading = false
                    displayError(event.errorText)
                }
                is ExploreViewModel.ExploreEvent.Loading -> {
                    binding.isLoading = true
                }
                else -> Unit
            }
        })
    }

    private fun displayError(message: String) {
        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("Retry") {
            viewModel.getExploreData()
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

    override fun onTableSelect(category: CategoryUtils.Categories) {
        val category = CategoryUtils.getCategory(category)
        val action = CategoryFragmentDirections.actionToCategory(category)
        findNavController().navigate(action)
    }
}