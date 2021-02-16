package com.xereon.xereon.ui.main.explore.items

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.data.category.source.CategoryConverter
import com.xereon.xereon.data.store.SimpleStore
import com.xereon.xereon.databinding.HorizontalStoreItemBinding

class HorizontalStoresAdapter : RecyclerView.Adapter<HorizontalStoresAdapter.VH>() {

    private lateinit var onClickAction: (SimpleStore) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            HorizontalStoreItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(differ.currentList[position])
    }


    private val diffCallback = object : DiffUtil.ItemCallback<SimpleStore>() {
        override fun areItemsTheSame(oldItem: SimpleStore, newItem: SimpleStore) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SimpleStore, newItem: SimpleStore) =
            oldItem.hashCode() == newItem.hashCode()
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun update(list: List<SimpleStore>) = differ.submitList(list)


    fun setOnStoreClickListener(onClickAction: (SimpleStore) -> Unit) {
        this.onClickAction = onClickAction
    }


    inner class VH(private val binding: HorizontalStoreItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(store: SimpleStore) {

            binding.apply {
                storeImage.clipToOutline = true
                Glide.with(storeImage.context).load(store.logoImageURL).into(storeImage)
                storeName.text = Html.fromHtml(store.name)

                storeType.text = store.type
                storeType.setTextColor(root.context.getColor(
                    CategoryConverter.getCategoryColor(store.category)
                ))

                root.setOnClickListener {
                    if (::onClickAction.isInitialized)
                        onClickAction.invoke(store)
                }
            }
        }
    }
}