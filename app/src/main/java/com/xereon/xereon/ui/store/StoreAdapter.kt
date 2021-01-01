package com.xereon.xereon.ui.store

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.databinding.RecyclerProductVerticalBinding

class StoreAdapter(
    private val store: Store
    ) : PagingDataAdapter<SimpleProduct, StoreAdapter.ViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == VIEW_TYPE_STORE)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemViewType(position: Int)
            = if (position == 0) VIEW_TYPE_STORE else VIEW_TYPE_PRODUCT


    inner class ViewHolderStore(private val binding: RecyclerProductVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currentProduct: SimpleProduct) {
            binding.product = currentProduct
        }
    }

    inner class ViewHolderProduct(private val binding: RecyclerProductVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currentProduct: SimpleProduct) {
            binding.product = currentProduct
        }
    }

    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<SimpleProduct>() {
            override fun areItemsTheSame(oldItem: SimpleProduct, newItem: SimpleProduct): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: SimpleProduct, newItem: SimpleProduct): Boolean {
                return oldItem == newItem
            }
        }
        private const val VIEW_TYPE_STORE = 0
        private const val VIEW_TYPE_PRODUCT = 1
    }
}