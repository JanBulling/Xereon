package com.xereon.xereon.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.databinding.RecyclerProductVerticalBinding
import java.util.AbstractMap

class ProductPagingAdapter :
    PagingDataAdapter<SimpleProduct, ProductPagingAdapter.ViewHolder>(COMPARATOR) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductPagingAdapter.ViewHolder {
        val binding = RecyclerProductVerticalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductPagingAdapter.ViewHolder, position: Int) {
        val currentItem = getItem(position)

        if (currentItem != null)
            holder.bind(currentItem)
    }


    inner class ViewHolder(private val binding: RecyclerProductVerticalBinding) :
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
    }
}