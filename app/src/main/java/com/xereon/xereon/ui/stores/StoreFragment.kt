package com.xereon.xereon.ui.stores

import android.content.Intent
import android.net.Uri
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
import com.xereon.xereon.util.DialogHelper
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

        binding.notFoundBtn.setOnClickListener {
            viewModel.getStoreData(args.storeId)
            binding.loading.isVisible = true
        }

        viewModel.storeItems.observeLiveData(this) {
            if (it.isNotEmpty()) {
                storeAdapter.update(it)
                binding.apply {
                    notFoundBtn.isVisible = false
                    notFoundImg.isVisible = false
                    notFoundText.isVisible = false
                    loading.isVisible = false
                }
            }
        }

        viewModel.events.observeLiveData(this) {
            when (it) {
                is StoreEvents.NavigateBack -> (requireActivity() as MainActivity).goBack()
                is StoreEvents.OpenNavigation -> {
                    val lat = it.latLng.latitude
                    val lng = it.latLng.longitude
                    //val uri = Uri.parse("google.navigation:q=$lat,$lng")
                    val uri = Uri.parse("geo:$lat,$lng?q=$lat,$lng&z=13")
                    val geoIntent =
                        Intent(Intent.ACTION_VIEW, uri).setPackage("com.google.android.apps.maps")
                    startActivity(geoIntent)
                }
                is StoreEvents.NavigateChat -> {
                    //doNavigate(StoreFragmentDirections.actionStoreFragmentToChatFragment)
                }
                is StoreEvents.NavigateToProducts -> {
                    doNavigate(
                        StoreFragmentDirections.actionStoreFragmentToStoreProductsFragment(
                            it.storeId, it.storeName
                        )
                    )
                }
                is StoreEvents.NavigateToProduct -> {
                    doNavigate(
                        StoreFragmentDirections.actionStoreFragmentToProductFragment(
                            it.productId, it.productName
                        )
                    )
                }
                is StoreEvents.StoreReported -> {
                    Snackbar.make(requireView(), "Filiale gemelden", Snackbar.LENGTH_LONG).show()
                }
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
        viewModel.storeName.observeLiveData(this) { binding.toolbar.title = it }
        viewModel.getStoreData(args.storeId)

        setupMenu()
    }

    private fun setupMenu() {
        binding.toolbar.apply {
            inflateMenu(R.menu.menu_store)

            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.menu_item_report_store -> {
                        showReportDialog()
                        true
                    }
                    else -> false
                }
            }
            title = args.storeName
        }
    }

    private fun showReportDialog() {
        val dialog = DialogHelper.DialogInstance(
            context = requireContext(),
            title = "Filiale melden?",
            message = "Melden Sie diese Filiale, wenn diese Ihrer Meinung nach gegen die Nutzungsbedinungen verstößt.",
            positiveButton = "Melden",
            positiveButtonFunction = {
                viewModel.reportStore(args.storeId)
            },
            negativeButton = "Abbrechen"
        )
        DialogHelper.showDialog(dialog)
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