package com.xereon.xereon.ui.main.explore.items

import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.util.PriceUtils
import com.xereon.xereon.databinding.HorizontalProductItemBinding

class HorizontalProductsAdapter : RecyclerView.Adapter<HorizontalProductsAdapter.VH>() {

    private lateinit var onClickAction: (SimpleProduct) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            HorizontalProductItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(differ.currentList[position])
    }


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


    inner class VH(private val binding: HorizontalProductItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: SimpleProduct) {

            binding.apply {
                Glide.with(productImage.context).load(product.productImageURL).into(productImage)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    productName.text = Html.fromHtml(product.name, Html.FROM_HTML_MODE_LEGACY)
                else
                    productName.text = product.name

                productPrice.text = PriceUtils.getPriceWithUnitAsString(product.price, product.unit)

                root.setOnClickListener {
                    if (::onClickAction.isInitialized)
                        onClickAction.invoke(product)
                }
            }
        }
    }
}