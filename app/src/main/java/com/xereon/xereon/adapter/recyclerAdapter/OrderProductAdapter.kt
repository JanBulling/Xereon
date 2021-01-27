package com.xereon.xereon.adapter.recyclerAdapter

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.data.util.PriceUtils
import com.xereon.xereon.databinding.RecyclerOrderProductBinding
import com.xereon.xereon.db.model.OrderProduct
import java.util.*

class OrderProductAdapter : RecyclerView.Adapter<OrderProductAdapter.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener
    private var mList: List<OrderProduct>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            RecyclerOrderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = mList?.size ?: 0


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList!![position])
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun submitList(list: List<OrderProduct>) {
        mList = list
        notifyDataSetChanged()
    }

    fun getList() = mList


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(clickListener: ItemClickListener) {
        itemClickListener = clickListener
    }

    interface ItemClickListener {
        fun onItemClick(order: OrderProduct)
        fun onItemCountIncreased(order: OrderProduct)
        fun onItemCountDecreased(order: OrderProduct)
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(
        val binding: RecyclerOrderProductBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (::itemClickListener.isInitialized) {
                    val currentIndex = bindingAdapterPosition
                    if (currentIndex != RecyclerView.NO_POSITION) {
                        val currentStore = mList!![currentIndex]
                        itemClickListener.onItemClick(currentStore)
                    }
                }
            }
        }

        fun bind(order: OrderProduct) {
            binding.apply {
                orderProductImg.clipToOutline = true
                Glide.with(orderProductImg).load(order.productImageURL).into(orderProductImg)

                orderProductName.text = order.name
                val numberProducts =
                    "Menge: <b>${PriceUtils.getCountWithUnit(order.unit, order.count)}</b>"
                orderProductCount.text = Html.fromHtml(numberProducts)

                val price = String.format(Locale.US  ?: null, "%.2f", order.totalPrice) + "€"
                orderProductTotalPrice.text = price

                val pricePerUnit = "je ${PriceUtils.getCompleteUnitAsString(order.unit)}: " +
                        String.format(Locale.US ?: null, "%.2f", order.price) + "€"
                orderProductPricePerUnit.text = pricePerUnit

                orderProductMore.setOnClickListener {
                    if (::itemClickListener.isInitialized)
                        itemClickListener.onItemCountIncreased(order)
                }
                orderProductLess.setOnClickListener {
                    if (::itemClickListener.isInitialized)
                        itemClickListener.onItemCountDecreased(order)
                }
            }
        }

    }
}