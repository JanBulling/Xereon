package com.xereon.xereon.adapter.recyclerAdapter

import android.text.Html
import android.util.Log.d
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.databinding.RecyclerOrdersBinding
import com.xereon.xereon.databinding.RecyclerSingleOrderBinding
import com.xereon.xereon.databinding.RecyclerStoreHorizontalBinding
import com.xereon.xereon.db.SimpleOrder
import com.xereon.xereon.db.StoreBasic
import com.xereon.xereon.util.Constants.TAG
import java.util.*

class OrderStoreAdapter : RecyclerView.Adapter<OrderStoreAdapter.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerOrdersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = differ.currentList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private val diffCallback = object: DiffUtil.ItemCallback<StoreBasic>() {
        override fun areItemsTheSame(oldItem: StoreBasic, newItem: StoreBasic) =
            oldItem.storeID == newItem.storeID

        override fun areContentsTheSame(oldItem: StoreBasic, newItem: StoreBasic) =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<StoreBasic>) {
        differ.submitList(list)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(clickListener: ItemClickListener) {
        itemClickListener = clickListener
    }
    interface ItemClickListener{
        fun onItemClick(store: StoreBasic)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(
        val binding: RecyclerOrdersBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val currentIndex = bindingAdapterPosition
                if (currentIndex != RecyclerView.NO_POSITION) {
                    val currentStore = differ.currentList[currentIndex]
                    if (currentStore != null)
                        itemClickListener.onItemClick(currentStore)
                }
            }
        }

        fun bind(store: StoreBasic) {
            binding.apply {
                orderStoreImg.clipToOutline = true
                Glide.with(orderStoreImg).load(store.logoImageURL).into(orderStoreImg)

                orderStoreName.text = store.storeName
                val numberProducts = "<font color=#252525>Anzahl Produkte: </font> <font color=#818181>${store.numberProducts}</font>"
                orderStoreNumberProducts.text = Html.fromHtml(numberProducts)

                val price = String.format("%.2f", store.sumPrice) + "€"
                orderStoreTotalPrice.text = price
            }
            binding.executePendingBindings()
        }

    }
}