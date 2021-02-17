package com.xereon.xereon.ui.main.favorites

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.paging.map
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.data.store.FavoriteStore
import com.xereon.xereon.databinding.FragmentFavoritesBinding
import com.xereon.xereon.ui.base.StorePagingAdapter
import com.xereon.xereon.util.Constants
import com.xereon.xereon.util.DialogHelper
import com.xereon.xereon.util.lists.decorations.TopBottomPaddingDecorator
import com.xereon.xereon.util.ui.doNavigate
import com.xereon.xereon.util.ui.observeLiveData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoritesFragment : Fragment(R.layout.fragment_favorites) {
    private val viewModel by viewModels<FavoritesFragmentViewModel>()

    private var _binding: FragmentFavoritesBinding? = null
    private val binding get() = _binding!!

    private val favoritesAdapter = StorePagingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentFavoritesBinding.bind(view)

        favoritesAdapter.addLoadStateListener { loadStates ->
            binding.empty.isVisible = loadStates.source.refresh is LoadState.NotLoading &&
                    loadStates.append.endOfPaginationReached &&
                    favoritesAdapter.itemCount < 1
        }

        ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(r: RecyclerView, v: RecyclerView.ViewHolder, t: RecyclerView.ViewHolder)
                    = false
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val favorite = favoritesAdapter.getItemAtPosition(viewHolder.bindingAdapterPosition)
                viewModel.deleteFavorite(favorite?.toFavoriteStore() ?: FavoriteStore())
            }
        }).attachToRecyclerView(binding.recyclerView)

        binding.recyclerView.apply {
            itemAnimator = null
            addItemDecoration(TopBottomPaddingDecorator(R.dimen.spacing_ultra_tiny))
            layoutManager = LinearLayoutManager(requireContext())
            adapter = favoritesAdapter
            setHasFixedSize(true)
        }

        favoritesAdapter.setOnStoreClickListener {
            doNavigate(FavoritesFragmentDirections.actionFavoritesFragmentToStoreFragment(it.id, it.name))
        }

        viewModel.favoriteStores.observeLiveData(this) {
            binding.empty.isVisible = false
            favoritesAdapter.submitData(viewLifecycleOwner.lifecycle, it.map { it.toSimpleStore() })
        }

        viewModel.events.observeLiveData(this) {
            if (it is FavoritesEvents.UndoDeleteMessage)
                showUndoDeleteMessageSnackbar(it.favorite)
        }

        setupMenu()
        viewModel.sortFavorites(Constants.SortType.RESPONSE_NEW_FIRST)
    }

    private fun setupMenu() {
        binding.toolbar.apply {
            inflateMenu(R.menu.menu_favorites)
            setOnMenuItemClickListener {
                when(it.itemId) {
                    R.id.menu_item_sort_new_first -> orderFavorites(Constants.SortType.RESPONSE_NEW_FIRST)
                    R.id.menu_item_sort_old_first -> orderFavorites(Constants.SortType.RESPONSE_OLD_FIRST)
                    R.id.menu_item_sort_a_z -> orderFavorites(Constants.SortType.RESPONSE_A_Z)
                    R.id.menu_item_sort_z_a -> orderFavorites(Constants.SortType.RESPONSE_Z_A)
                    R.id.menu_item_delete_favorites -> showDeleteAllDialog()
                    else -> false
                }
            }
        }
    }

    private fun orderFavorites(sortType: Constants.SortType): Boolean {
        binding.recyclerView.scrollToPosition(0)
        viewModel.sortFavorites(sortType)
        return true
    }

    private fun showDeleteAllDialog(): Boolean {
        val dialog = DialogHelper.DialogInstance(
            context = requireContext(),
            title = "Alle Favoriten löschen",
            message = "Möchten Sie wirklich alle gespeicherten Favoriten unwiderruflich entfernen?",
            positiveButton = "Löschen",
            positiveButtonFunction = { viewModel.deleteAllFavorites() },
            negativeButton = "Abbrechen"
        )
        DialogHelper.showDialog(dialog)
        return true
    }

    private fun showUndoDeleteMessageSnackbar(favorite: FavoriteStore) {
        Snackbar.make(requireView(), "Favorit gelöscht", Snackbar.LENGTH_LONG)
            .setAction("Rückgänig") {
                viewModel.undoDelete(favorite)
            }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}