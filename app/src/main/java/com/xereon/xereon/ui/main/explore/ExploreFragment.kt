package com.xereon.xereon.ui.main.explore

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.databinding.FragmentExploreBinding
import com.xereon.xereon.util.lists.decorations.TopBottomPaddingDecorator
import com.xereon.xereon.util.lists.diffutil.update
import com.xereon.xereon.util.ui.doNavigate
import com.xereon.xereon.util.ui.observeLiveData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExploreFragment : Fragment(R.layout.fragment_explore) {
    private val viewModel by viewModels<ExploreFragmentViewModel>()

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!

    private val exploreAdapter = ExploreAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentExploreBinding.bind(view)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(TopBottomPaddingDecorator(topPadding = R.dimen.spacing_mega_tiny))
            adapter = exploreAdapter
            setHasFixedSize(true)
        }

        viewModel.exploreItems.observeLiveData(this) {
            binding.loading.isVisible = false
            exploreAdapter.update(it)
        }

        viewModel.routeToScreen.observeLiveData(this) { doNavigate(it) }
        viewModel.exceptions.observeLiveData(this) { showError(it) }

        setupMenu()
    }

    private fun setupMenu() {
        binding.toolbar.apply {
            inflateMenu(R.menu.menu_explore)

            val chatActionView = menu.findItem(R.id.menu_item_chat).actionView
            val batch = chatActionView.findViewById<View>(R.id.chat_batch)

            viewModel.hasNewMessages.observeLiveData(this@ExploreFragment) { batch.isVisible = it }

            chatActionView.setOnClickListener {
                Toast.makeText(requireContext(), "To chat", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showError(@StringRes message: Int) {
        val snackBar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val snackBarView: View = snackBar.view
            snackBarView.setBackgroundColor(resources.getColor(R.color.error, null))
        }
        snackBar.show()
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