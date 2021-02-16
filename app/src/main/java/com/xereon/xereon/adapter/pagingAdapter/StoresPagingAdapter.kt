package com.xereon.xereon.adapter.pagingAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.data.store.SimpleStore
import com.xereon.xereon.data.util.CategoryUtils
import com.xereon.xereon.databinding.RecyclerStoreVerticalBinding

class StoresPagingAdapter() :
    PagingDataAdapter<SimpleStore, StoresPagingAdapter.ViewHolder>(COMPARATOR) {

    private lateinit var itemClickListener: ItemClickListener


    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RecyclerStoreVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_STORE) {
            val simpleStore = getItem(position)

            if (simpleStore != null)
                holder.bind(simpleStore)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            itemCount -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_STORE
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(clickListener: ItemClickListener) { itemClickListener = clickListener }
    interface ItemClickListener { fun onItemClick(simpleStore: SimpleStore) }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(private val binding: RecyclerStoreVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val currentIndex = bindingAdapterPosition
                if (currentIndex != RecyclerView.NO_POSITION) {
                    val item = getItem(currentIndex)
                    if (item != null)
                        itemClickListener.onItemClick(item)
                }
            }
        }

        fun bind(simpleStore: SimpleStore) {
            binding.apply {
                Glide.with(recyclerStoreImage).load(simpleStore.logoImageURL)
                    .into(recyclerStoreImage)
                recyclerStoreName.text = simpleStore.name
                recyclerStoreType.text = simpleStore.type
                @ColorRes val colorId = CategoryUtils.getCategoryColorResourceId(simpleStore.category)
                recyclerStoreType.setTextColor(ContextCompat.getColor(recyclerStoreType.context, colorId))
                recyclerStoreCity.text = simpleStore.city
            }
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    companion object {
        private val COMPARATOR = object : DiffUtil.ItemCallback<SimpleStore>() {
            override fun areItemsTheSame(oldItem: SimpleStore, newItem: SimpleStore) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SimpleStore, newItem: SimpleStore) =
                oldItem.hashCode() == newItem.hashCode()
        }

        const val VIEW_TYPE_STORE = 0
        const val VIEW_TYPE_LOADING = 1
    }
}