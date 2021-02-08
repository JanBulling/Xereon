package com.xereon.xereon.ui.favorites

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.adapter.loadStateAdapter.StoresLoadStateAdapter
import com.xereon.xereon.adapter.pagingAdapter.FavoritesPagingAdapter
import com.xereon.xereon.databinding.FrgFavoritesBinding
import com.xereon.xereon.db.model.FavoriteStore
import com.xereon.xereon.db.model.OrderProduct
import com.xereon.xereon.ui.shoppingCart.ShoppingCartViewModel
import com.xereon.xereon.util.Constants
import kotlinx.coroutines.flow.collect

class FavoritesFragment : Fragment(R.layout.frg_favorites) {

    private val viewModel by activityViewModels<FavoritesViewModel>()

    private var _binding: FrgFavoritesBinding? = null
    private val binding get() = _binding!!

    private val favoritesAdapter = FavoritesPagingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgFavoritesBinding.bind(view)

        favoritesAdapter.setOnItemClickListener(object : FavoritesPagingAdapter.ItemClickListener {
            override fun onItemClick(favoriteStore: FavoriteStore) {
                val action =
                    FavoritesFragmentDirections.actionToStore(favoriteStore.toSimpleStore())
                findNavController().navigate(action)
            }
        })
        favoritesAdapter.addLoadStateListener { loadStates ->
            //empty view
            binding.favoriteEmpty.isVisible = loadStates.source.refresh is LoadState.NotLoading &&
                    loadStates.append.endOfPaginationReached &&
                    favoritesAdapter.itemCount < 1

        }
        binding.apply {
            favoriteRecycler.apply {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                itemAnimator = null
                adapter = favoritesAdapter.withLoadStateFooter(
                    footer = StoresLoadStateAdapter { favoritesAdapter.retry() }
                )
            }

            ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT){
                override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder)
                        = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val favorite = favoritesAdapter.getItemAtPosition(viewHolder.bindingAdapterPosition) ?: FavoriteStore()
                    viewModel.deleteFavorite(favorite)
                }
            }).attachToRecyclerView(favoriteRecycler)
        }

        subscribeToObserver()

        setHasOptionsMenu(true)
    }

    private fun subscribeToObserver() {
        viewModel.favorites.observe(viewLifecycleOwner, Observer {
            favoritesAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.eventChannel.collect {event ->
                when (event) {
                    is FavoritesViewModel.FavoritesEvent.ShowUndoDeleteMessage -> {
                        Snackbar.make(requireView(), "Favorit entfernt", Snackbar.LENGTH_LONG)
                            .setAction("Rückgängig") {
                                viewModel.undoDelete(event.favorite)
                            }.setActionTextColor(Color.parseColor("#ADD8E6")).show()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_favorites, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        favoritesAdapter.submitData(viewLifecycleOwner.lifecycle, PagingData.empty())
        binding.favoriteRecycler.scrollToPosition(0)
        return when (item.itemId) {
            R.id.menu_item_new_first -> {
                viewModel.sortElements(Constants.SortType.RESPONSE_NEW_FIRST)
                true
            }
            R.id.menu_item_old_first -> {
                viewModel.sortElements(Constants.SortType.RESPONSE_OLD_FIRST)
                true
            }
            R.id.menu_item_order_a_z -> {
                viewModel.sortElements(Constants.SortType.RESPONSE_A_Z)
                true
            }
            R.id.menu_item_order_z_a -> {
                viewModel.sortElements(Constants.SortType.RESPONSE_Z_A)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}