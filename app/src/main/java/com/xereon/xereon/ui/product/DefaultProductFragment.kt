package com.xereon.xereon.ui.product

import android.os.Bundle
import android.util.Log.d
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.xereon.xereon.R
import com.xereon.xereon.data.model.Product
import com.xereon.xereon.databinding.FrgDefaultProductBinding
import com.xereon.xereon.ui.MainActivity
import com.xereon.xereon.utils.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DefaultProductFragment : Fragment(R.layout.frg_default_product) {
    private val viewModel by viewModels<ProductViewModel>()
    private val args by navArgs<DefaultProductFragmentArgs>()

    private var _binding: FrgDefaultProductBinding? = null
    private val binding get() = _binding!!

    private var productID: Int = -1
    private var productName: String = ""

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
        (activity as MainActivity).setBottomNavBarVisibility(false)
        (activity as MainActivity).setActionBarTitle(productName)

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
                    binding.product = dataState.data
                    binding.isLoading = false
                }
                is DataState.Loading -> {
                    binding.isLoading = true
                }
                is DataState.Error -> {
                    binding.isLoading = false
                    d("[APP_DEBUG]", "ERROR")
                }
            }
        })
    }
}