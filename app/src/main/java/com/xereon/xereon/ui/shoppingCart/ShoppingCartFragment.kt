package com.xereon.xereon.ui.shoppingCart

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.recyclerAdapter.OrderStoreAdapter
import com.xereon.xereon.databinding.FrgDefaultProductBinding
import com.xereon.xereon.databinding.FrgShoppingCartBinding
import com.xereon.xereon.db.StoreBasic
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frg_shopping_cart.*
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
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
                Toast.makeText(requireContext(), "Clicked on ${store.storeName}", Toast.LENGTH_SHORT).show()
            }
        })

        binding.apply {
            shoppingCartRecycler.apply {
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
            orderAdapter.submitList(stores)
        })

        viewModel.totalPrice.observe(viewLifecycleOwner, Observer {totalPrice ->
            val price = String.format("%.2f", totalPrice).replace(",", ".") + "€"
            binding.shoppingCartTotalPrice.text = price
        })
    }
}