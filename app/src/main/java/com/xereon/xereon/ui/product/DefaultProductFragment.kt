package com.xereon.xereon.ui.product

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.xereon.xereon.R
import com.xereon.xereon.adapter.recyclerAdapter.ProductHorizontalAdapter
import com.xereon.xereon.adapter.recyclerAdapter.ProductVerticalAdapter
import com.xereon.xereon.data.model.Product
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.util.PriceUtils
import com.xereon.xereon.databinding.FrgDefaultProductBinding
import com.xereon.xereon.ui._parent.MainActivity
import com.xereon.xereon.util.DataState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.frg_default_product.*

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
        if (args.simpleProduct == null)
            productID = args.simpleProductId
        else {
            productID = args.simpleProduct!!.id
            productName = args.simpleProduct!!.name
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgDefaultProductBinding.bind(view)
        (activity as MainActivity).setActionBarTitle(productName)

        similarAdapter.setOnItemClickListener(this)
        productsFromSameStoreAdapter.setOnItemClickListener(this)

        binding.storeNotFoundRetry.setOnClickListener {
            viewModel.getProductData(productID, true)
        }

        binding.productSimilarList.apply{
            setHasFixedSize(true)
            adapter = similarAdapter
        }

        binding.productProductsSameStore.adapter = productsFromSameStoreAdapter

        subscribeObserver()

        viewModel.getProductData(productID)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeObserver() {
        viewModel.productData.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Success<Product> -> {

                    (activity as MainActivity).setActionBarTitle(dataState.data.name)
                    productsFromSameStoreAdapter.submitList(dataState.data.otherStoreProducts)
                    similarAdapter.submitList(dataState.data.similar)
                    binding.product = dataState.data

                    binding.isLoading = false
                    binding.isSuccessful = true

                    setUpNumberPicker(dataState.data.unit, dataState.data.price)
                }
                is DataState.Loading -> {
                    binding.isLoading = true
                }
                is DataState.Error -> {
                    binding.isLoading = false
                    binding.isSuccessful = false
                }
            }
        })
    }

    private fun setUpNumberPicker(unit: Int, price: String) {
        val steps = PriceUtils.getStepsAsStringArray(unit)
        product_number_picker.apply {
            minValue = 0
            maxValue = steps.size - 1
            displayedValues = steps
            value = 1
            wrapSelectorWheel = false
            setOnValueChangedListener { _, _, newValue ->
                numberSelected = newValue
                product_order_price.text = PriceUtils.calculateTotalPrice(price.toFloat(), unit, newValue)
            }
        }
    }

    override fun onItemClick(simpleProduct: SimpleProduct) {
        val action = DefaultProductFragmentDirections.actionToProduct(simpleProduct)
        findNavController().navigate(action)
    }
}