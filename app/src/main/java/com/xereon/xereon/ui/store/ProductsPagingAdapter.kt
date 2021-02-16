package com.xereon.xereon.ui.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.model.LatLng
import com.xereon.xereon.data.products.SimpleProduct
import com.xereon.xereon.data.store.Store
import com.xereon.xereon.databinding.InclStoreRecyclerBinding
import com.xereon.xereon.databinding.RecyclerProductVerticalBinding
import com.xereon.xereon.adapter.util.CustomPagingDataAdapter
import com.xereon.xereon.data.util.PriceUtils

class ProductsPagingAdapter :
    CustomPagingDataAdapter<SimpleProduct, RecyclerView.ViewHolder>(COMPARATOR, offset = OFFSET) {

    private lateinit var itemClickListener: ItemClickListener
    private lateinit var store: Store


    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_STORE) {
            val binding = InclStoreRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            StoreViewHolder(binding)
        } else {
            val binding = RecyclerProductVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ViewHolderProduct(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_STORE)
            (holder as StoreViewHolder).bind(store, itemClickListener)
        else if (getItemViewType(position) == VIEW_TYPE_PRODUCT) {
            val currentItem = getItem(position - OFFSET)

            if (currentItem != null)
                (holder as ViewHolderProduct).bind(currentItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 -> VIEW_TYPE_STORE
            itemCount -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_PRODUCT
        }
    }

    override fun getItemCount() =
        OFFSET + super.getItemCount()


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun withCustomLoadStateFooter(
        footer: LoadStateAdapter<*>
    ) : ConcatAdapter {
        addLoadStateListener { loadStates ->

            if (loadStates.source.refresh is LoadState.NotLoading && itemCount <= 1) {
                footer.loadState = LoadState.Error(Throwable("empty"))
            }
            else
                footer.loadState = when (loadStates.refresh) {
                    is LoadState.NotLoading -> loadStates.append
                    else -> loadStates.refresh
                }
        }
        return ConcatAdapter(this, footer)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(clickListener: ItemClickListener) { itemClickListener = clickListener }
    interface ItemClickListener{
        fun onItemClick(simpleProduct: SimpleProduct)
        fun onAddToFavoriteClicked()
        fun onNavigationClicked(latitude: LatLng)
        fun onChatClicked()
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setStore(storeInsert: Store) {
        store = storeInsert
        notifyItemChanged(0)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolderProduct(private val binding: RecyclerProductVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val currentIndex = bindingAdapterPosition - OFFSET
                if (currentIndex != RecyclerView.NO_POSITION) {
                    val item = getItem(currentIndex)
                    if (item != null)
                        itemClickListener.onItemClick(item)
                }
            }
        }

        fun bind(currentProduct: SimpleProduct) {
            binding.apply {
                Glide.with(recyclerProductImg).load(currentProduct.productImageURL)
                    .into(recyclerProductImg)
                recyclerProductName.text = currentProduct.name
                val priceWithUnit = PriceUtils.getPriceWithUnitAsString(currentProduct.price, currentProduct.unit)
                recyclerProductPrice.text = priceWithUnit
                recyclerOnlyInApp.isVisible = currentProduct.appoffer
            }
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    companion object {

        private val COMPARATOR = object : DiffUtil.ItemCallback<SimpleProduct>() {
            override fun areItemsTheSame(oldItem: SimpleProduct, newItem: SimpleProduct)
                    = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SimpleProduct, newItem: SimpleProduct)
                    = oldItem.hashCode() == newItem.hashCode()
        }

        const val VIEW_TYPE_STORE = 0
        const val VIEW_TYPE_PRODUCT = 1
        const val VIEW_TYPE_LOADING = 2

        private const val OFFSET = 1
    }
}