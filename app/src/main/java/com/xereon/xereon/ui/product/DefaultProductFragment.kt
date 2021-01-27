package com.xereon.xereon.ui.product

import android.graphics.Color
import android.os.Bundle
import android.util.Log.e
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.adapter.recyclerAdapter.ProductHorizontalAdapter
import com.xereon.xereon.adapter.recyclerAdapter.ProductVerticalAdapter
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.util.PriceUtils
import com.xereon.xereon.databinding.FrgDefaultProductBinding
import com.xereon.xereon.ui._parent.MainActivity
import com.xereon.xereon.ui.shoppingCart.ShoppingCartViewModel
import com.xereon.xereon.util.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frg_default_product.*
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DefaultProductFragment : Fragment(R.layout.frg_default_product),
    ProductHorizontalAdapter.ItemClickListener,
    ProductVerticalAdapter.ItemClickListener {

    private val viewModel by viewModels<ProductViewModel>()
    private val args by navArgs<DefaultProductFragmentArgs>()

    private var _binding: FrgDefaultProductBinding? = null
    private val binding get() = _binding!!

    private var productID: Int = -1
    private var productName: String = ""
    private var numberSelected = 0

    private val similarAdapter = ProductHorizontalAdapter()
    private val productsFromSameStoreAdapter = ProductVerticalAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        productID = args.simpleProduct?.id ?: args.simpleProductId ?: -1
        productName = args.simpleProduct?.name ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgDefaultProductBinding.bind(view)
        (activity as MainActivity).setActionBarTitle(productName)

        similarAdapter.setOnItemClickListener(this)
        productsFromSameStoreAdapter.setOnItemClickListener(this)

        binding.apply {
            storeNotFoundRetry.setOnClickListener {
                viewModel.getProduct(productID)
            }
            productProductsSameStore.adapter = productsFromSameStoreAdapter
            productSimilarList.apply{
                setHasFixedSize(true)
                adapter = similarAdapter
            }
            productAddToCart.setOnClickListener { addToCart() }
            productAddToCartFab.setOnClickListener { addToCart() }
        }

        subscribeObserver()

        viewModel.getProduct(productID)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeObserver() {
        viewModel.productData.observe(viewLifecycleOwner, Observer {event ->
            when (event) {
                is ProductViewModel.ProductEvent.Success -> {
                    (activity as MainActivity).setActionBarTitle(event.productData.name)

                    binding.apply {
                        productLoading.isVisible = false
                        productScrollParent.isVisible = true
                        product = event.productData

                        productMoreStore.setOnClickListener {
                            val action = DefaultProductFragmentDirections.actionToStore(simpleStoreId = event.productData.storeID)
                            findNavController().navigate(action)
                        }
                        productStoreImg.setOnClickListener {
                            val action = DefaultProductFragmentDirections.actionToStore(simpleStoreId = event.productData.storeID)
                            findNavController().navigate(action)
                        }
                    }

                    productsFromSameStoreAdapter.submitList(event.productData.otherStoreProducts)
                    similarAdapter.submitList(event.productData.similar)

                    setUpNumberPicker(event.productData.unit, event.productData.price)
                }
                is ProductViewModel.ProductEvent.Failure -> {
                    binding.apply {
                        productLoading.isVisible = false
                        productError.isVisible = true
                    }
                }
                is ProductViewModel.ProductEvent.Loading -> {
                    binding.apply {
                        productLoading.isVisible = true
                        productError.isVisible = false
                    }
                }
                else -> Unit
            }
        })
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect {event ->
                when (event) {
                    is ProductViewModel.ProductEvent.ShowErrorMessage -> displayError(event.message)
                    else -> Unit
                }
            }
        }
    }

    private fun setUpNumberPicker(unit: Int, price: String) {
        val steps = PriceUtils.getStepsAsStringArray(unit)
        product_number_picker.apply {
            minValue = 0
            maxValue = steps.size - 1
            displayedValues = steps
            wrapSelectorWheel = false
            setOnValueChangedListener { _, _, newValue ->
                numberSelected = newValue
                product_order_price.text = PriceUtils.calculateTotalPriceAsString(price.toFloat(), unit, newValue + 1)
            }
        }
    }

    private fun addToCart() {
        viewModel.addToShoppingCart(numberSelected + 1)
        Snackbar.make(binding.root, "In den Warenkorb hinzugefüfgt", Snackbar.LENGTH_LONG)
            .setAction("Zum Warenkorb") {
                findNavController().navigate(R.id.action_to_ShoppingCart)
            }.setActionTextColor(Color.parseColor("#ADD8E6")).show()
    }

    override fun onItemClick(simpleProduct: SimpleProduct) {
        val action = DefaultProductFragmentDirections.actionToProduct(simpleProduct)
        findNavController().navigate(action)
    }

    private fun displayError(message: String) {
        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT)
        val snackBarView: View = snackBar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.error))
        snackBar.show()
    }
}