package com.xereon.xereon.ui.shoppingCart

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.recyclerAdapter.OrderStoreAdapter
import com.xereon.xereon.databinding.FrgShoppingCartBinding
import com.xereon.xereon.db.StoreBasic
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ShoppingCartFragment : Fragment(R.layout.frg_shopping_cart) {
    private val viewModel by viewModels<ShoppingCartViewModel>()

    private var _binding: FrgShoppingCartBinding? = null
    private val binding get() = _binding!!

    private val orderAdapter = OrderStoreAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FrgShoppingCartBinding.bind(view)

        orderAdapter.setOnItemClickListener(object: OrderStoreAdapter.ItemClickListener{
            override fun onItemClick(store: StoreBasic) {
                val action = ShoppingCartFragmentDirections.actionToShoppingCartProduct(store.id)
                findNavController().navigate(action)
            }
        })

        binding.apply {
            shoppingCartRecycler.apply {
                itemAnimator = null
                adapter = orderAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.getAllStores()
        viewModel.getTotalPrice()

        subscribeObserver()
    }

    private fun subscribeObserver() {
        viewModel.stores.observe(viewLifecycleOwner, Observer {stores ->
            binding.shoppingCartEmpty.isVisible = stores.isEmpty()
            orderAdapter.submitList(stores)
        })

        viewModel.totalPrice.observe(viewLifecycleOwner, Observer {totalPrice ->
            val price = String.format(Locale.US ?: null, "%.2f", totalPrice) + "€"
            binding.shoppingCartTotalPrice.text = price
        })
    }
}