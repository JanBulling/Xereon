package com.xereon.xereon.ui.stores

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.databinding.FragmentExploreBinding
import com.xereon.xereon.databinding.FragmentStoreBinding
import com.xereon.xereon.ui.main.MainActivity
import com.xereon.xereon.ui.main.explore.ExploreAdapter
import com.xereon.xereon.ui.main.explore.ExploreFragmentViewModel
import com.xereon.xereon.util.lists.decorations.TopBottomPaddingDecorator
import com.xereon.xereon.util.lists.diffutil.update
import com.xereon.xereon.util.ui.doNavigate
import com.xereon.xereon.util.ui.observeLiveData
import com.xereon.xereon.util.ui.showError
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoreFragment : Fragment(R.layout.fragment_store) {
    private val viewModel by viewModels<StoreFragmentViewModel>()

    private val args by navArgs<StoreFragmentArgs>()

    private var _binding: FragmentStoreBinding? = null
    private val binding get() = _binding!!

    private val storeAdapter = StoreAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentStoreBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            viewModel.onBackClick()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(TopBottomPaddingDecorator(topPadding = R.dimen.spacing_small))
            adapter = storeAdapter
            setHasFixedSize(true)
        }

        viewModel.storeItems.observeLiveData(this) {
            binding.loading.isVisible = false
            storeAdapter.update(it)
        }

        viewModel.events.observeLiveData(this) {
            when (it) {
                is StoreEvents.NavigateBack -> (requireActivity() as MainActivity).goBack()
            }
        }
        viewModel.exceptions.observeLiveData(this) { showError(it) }
        viewModel.storeName.observeLiveData(this) { binding.toolbar.title = it }
        viewModel.getStoreData(args.storeId)

        setupToolbar()
    }

    private fun setupToolbar() {
    }

    override fun onResume() {
        super.onResume()
        binding.container.sendAccessibilityEvent(AccessibilityEvent.TYPE_ANNOUNCEMENT)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}