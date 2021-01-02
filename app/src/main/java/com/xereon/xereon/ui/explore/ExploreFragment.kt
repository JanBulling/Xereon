package com.xereon.xereon.ui.explore

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.adapter.ProductHorizontalAdapter
import com.xereon.xereon.adapter.StoreHorizontalAdapter
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.databinding.FrgExploreBinding
import com.xereon.xereon.ui.MainActivity
import com.xereon.xereon.ui.store.DefaultStoreFragment
import com.xereon.xereon.utils.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment(R.layout.frg_explore), ProductHorizontalAdapter.OnClickListener {

    private val viewModel: ExploreViewModel by activityViewModels()

    private var _binding: FrgExploreBinding? = null
    private val binding get() = _binding!!

    private val newStoresAdapter: StoreHorizontalAdapter = StoreHorizontalAdapter(

        object : StoreHorizontalAdapter.OnClickListener {
            override fun onClick(store: SimpleStore) {
                val bundle = Bundle();
                if (store.id == 334114)
                    bundle.putInt(DefaultStoreFragment.CURRENT_STORE_ID, 57)
                else
                    bundle.putInt(DefaultStoreFragment.CURRENT_STORE_ID, store.id)

                bundle.putString(DefaultStoreFragment.CURRENT_STORE_NAME, store.name)
                findNavController().navigate(R.id.Action_to_Store, bundle)
            }
        }

    )

    private val recommendationsAdapter: ProductHorizontalAdapter = ProductHorizontalAdapter(this)
    private val popularAdapter: ProductHorizontalAdapter = ProductHorizontalAdapter(this)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgExploreBinding.bind(view)
        (activity as MainActivity).setBottomNavBarVisibility(true)

        binding.exploreRecyclerNewStores.adapter = newStoresAdapter
        binding.exploreRecyclerPopular.adapter = popularAdapter
        binding.exploreRecyclerRecommendations.adapter = recommendationsAdapter

        subscribeObserver()

        if (savedInstanceState == null)
            viewModel.getExploreData(1, "89542")
    }

    private fun subscribeObserver() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            when (dataState) {
                is DataState.Success<ExploreData> -> {
                    binding.isLoading = false
                    newStoresAdapter.setList(dataState.data.newStores)
                    recommendationsAdapter.setList(dataState.data.recommendations)
                    popularAdapter.setList(dataState.data.popular)
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
        snackBar.setActionTextColor(resources.getColor(R.color.white))
        val snackBarView: View = snackBar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.error))
        snackBar.show()
    }

    override fun onClick(store: SimpleProduct) {
        Toast.makeText(context, "Clicked on: ${store.name}", Toast.LENGTH_SHORT).show()
    }
}