package com.xereon.xereon.adapter.recyclerAdapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.util.PriceUtils
import com.xereon.xereon.databinding.RecyclerProductHorizontalBinding
import com.xereon.xereon.databinding.RecyclerProductVerticalBinding

class ProductVerticalAdapter : RecyclerView.Adapter<ProductVerticalAdapter.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener


    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerProductVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() =
        if (differ.currentList.size > MAX_LIST_SIZE) MAX_LIST_SIZE else  differ.currentList.size


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private val diffCallback = object: DiffUtil.ItemCallback<SimpleProduct>() {
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
    fun setOnItemClickListener(clickListener: ItemClickListener) { itemClickListener = clickListener }
    interface ItemClickListener{ fun onItemClick(simpleProduct: SimpleProduct) }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder( val binding: RecyclerProductVerticalBinding)
        : RecyclerView.ViewHolder(binding.root) {

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
                recyclerOnlyInApp.isVisible = simpleProduct.appoffer
            }
        }
    }

    companion object {
        const val MAX_LIST_SIZE = 12
    }
}