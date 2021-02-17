package com.xereon.xereon.ui.base

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.data.category.util.CategoryConverter
import com.xereon.xereon.data.store.SimpleStore
import com.xereon.xereon.databinding.StoreListItemBinding

class StorePagingAdapter : PagingDataAdapter<SimpleStore, StorePagingAdapter.VH>(COMPARATOR) {
    private lateinit var onClickAction: (SimpleStore) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            StoreListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        if (item != null) holder.bind(item)
    }

    override fun getItemViewType(position: Int) = when (position) {
        itemCount -> VIEW_TYPE_LOADING
        else -> VIEW_TYPE_STORE
    }

    fun setOnStoreClickListener(onClickAction: (SimpleStore) -> Unit) {
        this.onClickAction = onClickAction
    }

    fun getItemAtPosition(position: Int) = getItem(position)

    inner class VH(private val binding: StoreListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            if (::onClickAction.isInitialized) {
                binding.root.setOnClickListener {
                    val index = absoluteAdapterPosition
                    if (index == RecyclerView.NO_POSITION) return@setOnClickListener
                    val item = getItem(index) ?: return@setOnClickListener
                    onClickAction.invoke(item)
                }
            }
        }

        fun bind(store: SimpleStore) {
            binding.apply {
                Glide.with(storeImage.context).load(store.logoImageURL).into(storeImage)
                storeName.text = Html.fromHtml(store.name)

                storeType.text = store.type
                storeType.setTextColor(root.context.getColor(
                    CategoryConverter.getCategoryColor(store.category)
                ))

                storeCity.text = store.city
            }
        }
    }

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