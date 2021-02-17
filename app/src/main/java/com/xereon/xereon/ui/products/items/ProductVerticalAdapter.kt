package com.xereon.xereon.ui.products.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.data.products.SimpleProduct
import com.xereon.xereon.data.util.PriceUtils
import com.xereon.xereon.databinding.ProductsListItemBinding

class ProductVerticalAdapter : RecyclerView.Adapter<ProductVerticalAdapter.VH>() {
    private lateinit var onClickAction: (SimpleProduct) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            ProductsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    override fun getItemCount() = differ.currentList.size


    private val diffCallback = object : DiffUtil.ItemCallback<SimpleProduct>() {
        override fun areItemsTheSame(oldItem: SimpleProduct, newItem: SimpleProduct) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: SimpleProduct, newItem: SimpleProduct) =
            oldItem.hashCode() == newItem.hashCode()
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun update(list: List<SimpleProduct>) = differ.submitList(list)


    fun setOnProductClickListener(onClickAction: (SimpleProduct) -> Unit) {
        this.onClickAction = onClickAction
    }

    inner class VH(private val binding: ProductsListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(product: SimpleProduct) {
            binding.apply {
                Glide.with(productImage.context).load(product.productImageURL)
                    .into(productImage)
                productName.text = product.name
                productPrice.text = PriceUtils.getPriceWithUnitAsString(product.price, product.unit)
                productAppExclusive.isVisible = product.appoffer

                root.setOnClickListener {
                    if (::onClickAction.isInitialized)
                        onClickAction.invoke(product)
                }
            }
        }

    }
}