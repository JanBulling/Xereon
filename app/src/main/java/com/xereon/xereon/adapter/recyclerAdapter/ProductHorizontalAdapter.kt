package com.xereon.xereon.adapter.recyclerAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.data.products.SimpleProduct
import com.xereon.xereon.data.util.PriceUtils
import com.xereon.xereon.databinding.RecyclerProductHorizontalBinding

class ProductHorizontalAdapter() : RecyclerView.Adapter<ProductHorizontalAdapter.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener


    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerProductHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() =
        if (differ.currentList.size > MAX_LIST_SIZE) MAX_LIST_SIZE else differ.currentList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private val diffCallback = object : DiffUtil.ItemCallback<SimpleProduct>() {
        override fun areItemsTheSame(oldItem: SimpleProduct, newItem: SimpleProduct) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SimpleProduct, newItem: SimpleProduct) =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<SimpleProduct>) {
        differ.submitList(list)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(clickListener: ItemClickListener) {
        itemClickListener = clickListener
    }

    interface ItemClickListener {
        fun onItemClick(simpleProduct: SimpleProduct)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(val binding: RecyclerProductHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (::itemClickListener.isInitialized) {
                    val currentIndex = bindingAdapterPosition
                    if (currentIndex != RecyclerView.NO_POSITION) {
                        val currentItem = differ.currentList[currentIndex]
                        if (currentItem != null)
                            itemClickListener.onItemClick(currentItem)
                    }
                }
            }
        }

        fun bind(simpleProduct: SimpleProduct) {
            binding.apply {
                Glide.with(recyclerProductImg).load(simpleProduct.productImageURL)
                    .into(recyclerProductImg)
                recyclerProductName.text = simpleProduct.name
                val priceWithUnit = PriceUtils.getPriceWithUnitAsString(simpleProduct.price, simpleProduct.unit)
                recyclerProductPrice.text = priceWithUnit
            }
        }
    }

    companion object {
        const val MAX_LIST_SIZE = 10
    }
}