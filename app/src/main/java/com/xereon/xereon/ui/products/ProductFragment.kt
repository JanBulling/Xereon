package com.xereon.xereon.ui.products

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.databinding.FragmentProductBinding
import com.xereon.xereon.ui.main.MainActivity
import com.xereon.xereon.ui.stores.StoreEvents
import com.xereon.xereon.util.lists.decorations.TopBottomPaddingDecorator
import com.xereon.xereon.util.lists.diffutil.update
import com.xereon.xereon.util.ui.observeLiveData
import com.xereon.xereon.util.ui.showError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductFragment : Fragment(R.layout.fragment_product) {
    private val viewModel by viewModels<ProductFragmentViewModel>()
    private val args by navArgs<ProductFragmentArgs>()

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private val productAdapter = ProductAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentProductBinding.bind(view)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(TopBottomPaddingDecorator(topPadding = R.dimen.spacing_small))
            adapter = productAdapter
            setHasFixedSize(true)
        }

        binding.notFoundBtn.setOnClickListener {
            viewModel.getProductData(args.productId)
            binding.loading.isVisible = true
        }

        viewModel.productItems.observeLiveData(this) {
            if (it.isNotEmpty()) {
                binding.apply {
                    notFoundBtn.isVisible = false
                    notFoundImg.isVisible = false
                    notFoundText.isVisible = false
                    loading.isVisible = false
                }
                productAdapter.update(it)
            }
        }

        viewModel.exceptions.observeLiveData(this) {
            binding.apply {
                notFoundBtn.isVisible = true
                notFoundImg.isVisible = true
                notFoundText.isVisible = true
                loading.isVisible = false
            }
            showError(it)
        }

        viewModel.events.observeLiveData(this) {
            when (it) {
                is ProductEvents.NavigateBack -> (requireActivity() as MainActivity).goBack()
            }
        }

        setupMenu()
        viewModel.getProductData(args.productId)
    }

    private fun setupMenu() {
        binding.toolbar.apply {
            setNavigationOnClickListener {
                viewModel.onBackClick()
            }
            title = args.productName
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}