package com.xereon.xereon.ui.shoppingCart

import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.adapter.recyclerAdapter.OrderProductAdapter
import com.xereon.xereon.databinding.FrgShoppingCartProductBinding
import com.xereon.xereon.data.products.OrderProduct
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.util.*

@AndroidEntryPoint
class ShoppingCartProductsFragment : Fragment(R.layout.frg_shopping_cart_product), OrderProductAdapter.ItemClickListener {
    private val viewModel by viewModels<ShoppingCartViewModel>()
    //private val args by navArgs<ShoppingCartProductsFragmentArgs>()

    private var _binding: FrgShoppingCartProductBinding? = null
    private val binding get() = _binding!!

    private val orderAdapter = OrderProductAdapter()

    private var storeId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //storeId = args.storeId
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FrgShoppingCartProductBinding.bind(view)

        orderAdapter.setOnItemClickListener(this)

        binding.apply {
            shoppingCartProductRecycler.apply {
                itemAnimator = null
                adapter = orderAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }

            ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
                override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder)
                        = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val order = orderAdapter.getList()?.get(viewHolder.bindingAdapterPosition) ?: OrderProduct()
                    viewModel.deleteOrder(order)
                }
            }).attachToRecyclerView(shoppingCartProductRecycler)

            shoppingCartProductStoreContinueShopping.setOnClickListener {
                //val action = ShoppingCartProductsFragmentDirections.actionToStore(simpleStoreId = storeId)
                //findNavController().navigate(action)
            }

            shoppingCartProductStoreCheckOut.setOnClickListener {
                //findNavController().navigate(R.id.action_ShoppingCart_to_Order)
            }
        }

        viewModel.getAllOrdersFromStore(storeId)
        viewModel.getStore(storeId)

        subscribeObserver()

        setHasOptionsMenu(true)
    }

    private fun subscribeObserver() {
        viewModel.productsFromStore.observe(viewLifecycleOwner, Observer {products ->
            binding.shoppingCartProductEmpty.isVisible = products.isEmpty()
            orderAdapter.submitList(products)
        })
        viewModel.store.observe(viewLifecycleOwner, Observer {store ->
            binding.apply {
                val numberProducts = "Anzahl Produkte: <font color=#252525><b>${store.numberProducts}</b></font>"
                val price = String.format(Locale.US ?: null, "%.2f", store.sumPrice) + "€"

                shoppingCartProductStoreImg.clipToOutline = true
                Glide.with(requireContext()).load(store.logoImageURL).into(shoppingCartProductStoreImg)
                shoppingCartProductStoreName.text = store.name
                shoppingCartProductStoreNumberProducts.text = Html.fromHtml(numberProducts)
                shoppingCartProductStoreTotalPrice.text = price
            }
        })

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect {event ->
                when (event) {
                    is ShoppingCartViewModel.ShoppingCartEvent.ShowUndoDeleteMessage -> {
                        Snackbar.make(requireView(), "Aus dem Warenkorb entfernt", Snackbar.LENGTH_LONG)
                            .setAction("Rückgängig") {
                                viewModel.undoDeleteOrder(event.order)
                            }.setActionTextColor(Color.parseColor("#ADD8E6")).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_shopping_cart_products, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_item_delete_all -> {
                //val action = ShoppingCartProductsFragmentDirections.actionToDeleteAllDialog(storeId)
                //findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onItemClick(order: OrderProduct) {
        //val action = ShoppingCartProductsFragmentDirections.actionToProduct(simpleProductId = order.id)
        //findNavController().navigate(action)
    }

    override fun onItemCountIncreased(order: OrderProduct) {
        viewModel.increaseOrderCount(order)
    }

    override fun onItemCountDecreased(order: OrderProduct) {
        viewModel.decreaseOrderCount(order)
    }

}