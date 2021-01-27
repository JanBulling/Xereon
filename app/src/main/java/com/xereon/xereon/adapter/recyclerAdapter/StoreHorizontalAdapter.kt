package com.xereon.xereon.adapter.recyclerAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.data.util.CategoryUtils
import com.xereon.xereon.databinding.RecyclerStoreHorizontalBinding

class StoreHorizontalAdapter : RecyclerView.Adapter<StoreHorizontalAdapter.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener


    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerStoreHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() =
        if (differ.currentList.size > MAX_LIST_SIZE) MAX_LIST_SIZE else  differ.currentList.size


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private val diffCallback = object: DiffUtil.ItemCallback<SimpleStore>() {
        override fun areItemsTheSame(oldItem: SimpleStore, newItem: SimpleStore) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SimpleStore, newItem: SimpleStore) =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<SimpleStore>) {
        differ.submitList(list)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(clickListener: ItemClickListener) { itemClickListener = clickListener }
    interface ItemClickListener{ fun onItemClick(simpleStore: SimpleStore) }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(
        val binding: RecyclerStoreHorizontalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (::itemClickListener.isInitialized) {
                    val currentIndex = bindingAdapterPosition
                    if (currentIndex != RecyclerView.NO_POSITION) {
                        val currentItem = differ.currentList[currentIndex]
                        itemClickListener.onItemClick(currentItem)
                    }
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
            }
        }
    }


    companion object {
        const val MAX_LIST_SIZE = 10
    }
}