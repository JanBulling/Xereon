package com.xereon.xereon.ui.explore

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.xereon.xereon.R
import com.xereon.xereon.adapter.StoreHorizontalAdapter
import com.xereon.xereon.data.model.ExploreData
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.databinding.FrgExploreBinding
import com.xereon.xereon.ui.MainActivity
import com.xereon.xereon.ui.store.DefaultStoreFragment
import com.xereon.xereon.utils.DataState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment(R.layout.frg_explore) {

    private val viewModel: ExploreViewModel by activityViewModels()

    private var _binding: FrgExploreBinding? = null
    private val binding get() = _binding!!

    private val adapter: StoreHorizontalAdapter = StoreHorizontalAdapter(

        object: StoreHorizontalAdapter.OnClickListener {
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


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgExploreBinding.bind(view)
        (activity as MainActivity).setBottomNavBarVisibility(true)

        binding.exploreRecyclerNewStores.adapter = adapter

        subscribeObserver()

        if (savedInstanceState == null)
            viewModel.getExploreData(1, "89542")
    }

    private fun subscribeObserver() {
        viewModel.dataState.observe(viewLifecycleOwner, Observer { dataState ->
            when(dataState) {
                is DataState.Success<ExploreData> -> {
                    Log.d("[APP DEBUG]", "successful: " + dataState.data.toString())

                    binding.isLoading = false
                    adapter.setList(dataState.data.newStores)
                }
                is DataState.Loading -> {
                    Log.d("[APP DEBUG]", "loading...")
                    binding.isLoading = true
                }
                is DataState.Error -> {
                    binding.isLoading  = false
                    Log.e("[APP DEBUG]", "error: " + dataState.exception.message)
                }
            }
        })
    }
}