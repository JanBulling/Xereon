package com.xereon.xereon.ui.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.databinding.RecyclerStoreVerticalBinding

class StoresPagingAdapter : PagingDataAdapter<SimpleStore, StoresPagingAdapter.ViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerStoreVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val simpleStore = getItem(position)

        if (simpleStore != null)
            holder.bind(simpleStore)
    }

    inner class ViewHolder(private val binding: RecyclerStoreVerticalBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(simpleStore: SimpleStore) {
            binding.store = simpleStore
        }

    }

    companion object {
        private val COMPARATOR = object: DiffUtil.ItemCallback<SimpleStore>() {
            override fun areItemsTheSame(oldItem: SimpleStore, newItem: SimpleStore) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SimpleStore, newItem: SimpleStore) =
                oldItem.hashCode() == newItem.hashCode()
        }
    }
}