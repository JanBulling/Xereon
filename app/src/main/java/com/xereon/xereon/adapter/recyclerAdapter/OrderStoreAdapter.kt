package com.xereon.xereon.adapter.recyclerAdapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.databinding.RecyclerOrderStoreBinding
import com.xereon.xereon.storage.StoreBasic
import java.util.*

class OrderStoreAdapter : RecyclerView.Adapter<OrderStoreAdapter.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerOrderStoreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = differ.currentList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private val diffCallback = object: DiffUtil.ItemCallback<StoreBasic>() {
        override fun areItemsTheSame(oldItem: StoreBasic, newItem: StoreBasic) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: StoreBasic, newItem: StoreBasic) =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<StoreBasic>) {
        differ.submitList(list)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(clickListener: ItemClickListener) { itemClickListener = clickListener }
    interface ItemClickListener{ fun onItemClick(store: StoreBasic) }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(
        val binding: RecyclerOrderStoreBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (::itemClickListener.isInitialized) {
                    val currentIndex = bindingAdapterPosition
                    if (currentIndex != RecyclerView.NO_POSITION) {
                        val currentStore = differ.currentList[currentIndex]
                        if (currentStore != null)
                            itemClickListener.onItemClick(currentStore)
                    }
                }
            }
        }

        fun bind(store: StoreBasic) {
            binding.apply {
                orderStoreImg.clipToOutline = true
                Glide.with(orderStoreImg).load(store.logoImageURL).into(orderStoreImg)

                orderStoreName.text = store.name
                val numberProducts = "Anzahl Produkte: <font color=#252525><b>${store.numberProducts}</b></font>"
                orderStoreNumberProducts.text = Html.fromHtml(numberProducts)

                val price = String.format(Locale.US ?: null, "%.2f", store.sumPrice) + "€"
                orderStoreTotalPrice.text = price
            }
        }

    }
}