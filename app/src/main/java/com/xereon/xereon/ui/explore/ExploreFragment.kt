package com.xereon.xereon.ui.explore

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.adapter.recyclerAdapter.ProductHorizontalAdapter
import com.xereon.xereon.adapter.recyclerAdapter.StoreHorizontalAdapter
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.data.util.CategoryUtils
import com.xereon.xereon.databinding.FrgExploreBinding
import com.xereon.xereon.di.InjectCity
import com.xereon.xereon.di.InjectPostCode
import com.xereon.xereon.di.InjectUserId
import com.xereon.xereon.ui.categories.CategoryFragmentDirections
import com.xereon.xereon.ui.product.DefaultProductFragmentDirections
import com.xereon.xereon.ui.store.DefaultStoreFragmentDirections
import com.xereon.xereon.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

@AndroidEntryPoint
class ExploreFragment : Fragment(R.layout.frg_explore), ProductHorizontalAdapter.ItemClickListener,
    OnTableItemSelect {
    private val viewModel by activityViewModels<ExploreViewModel>()

    private var _binding: FrgExploreBinding? = null
    private val binding get() = _binding!!

    private val newStoresAdapter = StoreHorizontalAdapter()
    private val recommendationsAdapter = ProductHorizontalAdapter()
    private val popularAdapter = ProductHorizontalAdapter()

    private lateinit var batch: View

    @JvmField @Inject @InjectUserId var userId: Int = Constants.DEFAULT_USER_ID
    @JvmField @Inject @InjectPostCode var postcode: String = Constants.DEFAULT_POSTCODE
    @JvmField @Inject @InjectCity var city: String = Constants.DEFAULT_CITY

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgExploreBinding.bind(view)

        recommendationsAdapter.setOnItemClickListener(this)
        popularAdapter.setOnItemClickListener(this)
        newStoresAdapter.setOnItemClickListener(object : StoreHorizontalAdapter.ItemClickListener {
            override fun onItemClick(simpleStore: SimpleStore) {
                val action = DefaultStoreFragmentDirections.actionToStore(simpleStore)
                findNavController().navigate(action)
            }
        })

        binding.apply {
            exploreTitleNewStores.text = getString(R.string.new_stores, city)
            exploreAllCategories.setOnClickListener {
                findNavController().navigate(R.id.action_to_AllCategories)
            }
            tableCategoryClickListener = this@ExploreFragment
        }

        setupRecycler()

        subscribeObserver()
        viewModel.getExploreData(userId, postcode)

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_explore, menu)

        val actionView = menu.findItem(R.id.menu_item_shopping_cart).actionView
        val textView = actionView.findViewById<TextView>(R.id.cart_badge)

        viewModel.ordersCount.observe(viewLifecycleOwner, Observer { numberOrder ->
            val text = if (numberOrder >= 100)
                "99"
            else
                "$numberOrder"
            textView.text = text
        })

        actionView.setOnClickListener {
            findNavController().navigate(R.id.action_to_ShoppingCart)
        }

        val actionViewChat = menu.findItem(R.id.menu_item_chat).actionView
        batch = actionViewChat.findViewById<View>(R.id.chat_batch)
        actionViewChat.setOnClickListener {
            findNavController().navigate(R.id.action_Explore_to_ChatOverview)
        }
    }

    private fun subscribeObserver() {
        viewModel.exploreData.observe(viewLifecycleOwner, Observer { event ->
            when (event) {
                is ExploreViewModel.ExploreEvent.Success -> {
                    binding.apply {
                        isLoading = false
                        exploreTitle.text = event.exploreData.title
                        val fontColor: Int = Color.parseColor(event.exploreData.fontColor)
                        val backgroundColor: Int = Color.parseColor(event.exploreData.backgroundColor)
                        exploreTitle.setTextColor(fontColor)
                        exploreTitleContainer.setBackgroundColor(backgroundColor)
                    }
                    newStoresAdapter.submitList(event.exploreData.newStores)
                    recommendationsAdapter.submitList(event.exploreData.recommendations)
                    popularAdapter.submitList(event.exploreData.popular)

                    batch.isVisible = event.exploreData.chatNewMessages
                }
                is ExploreViewModel.ExploreEvent.Failure -> {
                    binding.isLoading = false
                }
                is ExploreViewModel.ExploreEvent.Loading -> {
                    binding.isLoading = true
                }
                else -> Unit
            }
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect { event ->
                when (event) {
                    is ExploreViewModel.ExploreEvent.ShowErrorMessage -> {
                        displayError(messageId = event.errorMessageId)
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setupRecycler() {
        binding.apply {
            exploreRecyclerNewStores.apply {
                setHasFixedSize(true)
                adapter = newStoresAdapter
            }
            exploreRecyclerPopular.apply {
                setHasFixedSize(true)
                adapter = popularAdapter
            }
            exploreRecyclerRecommendations.apply {
                setHasFixedSize(true)
                adapter = recommendationsAdapter
            }
        }
    }

    private fun displayError(@StringRes messageId: Int) {
        val snackBar = Snackbar.make(requireView(), messageId, Snackbar.LENGTH_LONG)
        snackBar.setAction("Retry") {
            viewModel.getExploreData(userId, postcode)
        }
        snackBar.setActionTextColor(Color.WHITE)
        val snackBarView: View = snackBar.view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            snackBarView.setBackgroundColor(resources.getColor(R.color.error, null))
        snackBar.show()
    }

    override fun onItemClick(simpleProduct: SimpleProduct) {
        val action = DefaultProductFragmentDirections.actionToProduct(simpleProduct)
        findNavController().navigate(action)
    }

    override fun onTableSelect(category: CategoryUtils.Categories) {
        val selectedCategory = CategoryUtils.getCategory(category)
        val action = CategoryFragmentDirections.actionToCategory(selectedCategory)
        findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}