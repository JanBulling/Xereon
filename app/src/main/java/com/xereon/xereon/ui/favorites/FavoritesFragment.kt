package com.xereon.xereon.ui.favorites

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.loadStateAdapter.StoresLoadStateAdapter
import com.xereon.xereon.adapter.pagingAdapter.FavoritesPagingAdapter
import com.xereon.xereon.adapter.pagingAdapter.StoresPagingAdapter
import com.xereon.xereon.adapter.recyclerAdapter.OrderStoreAdapter
import com.xereon.xereon.databinding.FrgExploreBinding
import com.xereon.xereon.databinding.FrgFavoritesBinding
import com.xereon.xereon.databinding.FrgShoppingCartBinding
import com.xereon.xereon.db.model.FavoriteStore
import com.xereon.xereon.ui.shoppingCart.DeleteAllOrdersViewModel
import com.xereon.xereon.util.Constants

class FavoritesFragment : Fragment(R.layout.frg_favorites) {

    private val viewModel by activityViewModels<FavoritesViewModel>()

    private var _binding: FrgFavoritesBinding? = null
    private val binding get() = _binding!!

    private val storeAdapter = FavoritesPagingAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgFavoritesBinding.bind(view)

        binding.apply {
            favoriteRecycler.apply {
                itemAnimator = null
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
                adapter = storeAdapter.withLoadStateFooter(
                    footer = StoresLoadStateAdapter { storeAdapter.retry() }
                )
            }
        }
        storeAdapter.setOnItemClickListener(object : FavoritesPagingAdapter.ItemClickListener {
            override fun onItemClick(favoriteStore: FavoriteStore) {
                val action =
                    FavoritesFragmentDirections.actionToStore(favoriteStore.toSimpleStore())
                findNavController().navigate(action)
            }
        })
        storeAdapter.addLoadStateListener { loadStates ->
            //empty view
            binding.favoriteEmpty.isVisible = loadStates.source.refresh is LoadState.NotLoading &&
                    loadStates.append.endOfPaginationReached &&
                    storeAdapter.itemCount < 1

        }

        subscribeToObserver()

        setHasOptionsMenu(true)
    }

    private fun subscribeToObserver() {
        viewModel.favorites.observe(viewLifecycleOwner, Observer {
            storeAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_favorites, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_item_new_first -> {
                viewModel.sortElements(Constants.SortTypes.SORT_RESPONSE_NEW_FIRST)
                true
            }
            R.id.menu_item_old_first -> {
                viewModel.sortElements(Constants.SortTypes.SORT_RESPONSE_OLD_FIRST)
                true
            }
            R.id.menu_item_order_a_z -> {
                viewModel.sortElements(Constants.SortTypes.SORT_RESPONSE_A_Z)
                true
            }
            R.id.menu_item_order_z_a -> {
                viewModel.sortElements(Constants.SortTypes.SORT_RESPONSE_Z_A)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

}