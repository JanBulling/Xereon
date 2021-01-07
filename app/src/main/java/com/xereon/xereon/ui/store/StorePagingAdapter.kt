package com.xereon.xereon.ui.store

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.databinding.InclStoreRecyclerBinding
import com.xereon.xereon.databinding.RecyclerProductVerticalBinding
import com.xereon.xereon.adapter.util.CustomPagingDataAdapter
import com.xereon.xereon.data.util.OpeningUtils
import java.util.*

class StorePagingAdapter(
    private val mListener: OnClickListener
) : CustomPagingDataAdapter<SimpleProduct, RecyclerView.ViewHolder>(COMPARATOR, offset = OFFSET) {

    private var store: Store? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == VIEW_TYPE_STORE) {
            val binding =
                InclStoreRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolderStore(binding)
        } else {
            val binding = RecyclerProductVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolderProduct(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_STORE)
            (holder as ViewHolderStore).bind()
        else if (getItemViewType(position) == VIEW_TYPE_PRODUCT) {
            val currentItem = getItem(position - OFFSET)

            if (currentItem != null)
                (holder as ViewHolderProduct).bind(currentItem)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
            0 ->  VIEW_TYPE_STORE
            itemCount -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_PRODUCT
        }
    }

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

    override fun getItemCount() = OFFSET + super.getItemCount()

    interface OnClickListener {
        fun onClick(product: SimpleProduct)
    }

    fun setStore(storeInsert: Store) {
        store = storeInsert
        notifyItemChanged(0)
    }

    inner class ViewHolderStore(private val binding: InclStoreRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind() {
            binding.store = store
            binding.currentDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
            binding.openingTimes = OpeningUtils.getOpeningTimes(store?.openinghours!!)
        }
    }

    inner class ViewHolderProduct(private val binding: RecyclerProductVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(currentProduct: SimpleProduct) {
            binding.product = currentProduct
            binding.clickListener = mListener
        }
    }

    companion object {

        private val COMPARATOR = object : DiffUtil.ItemCallback<SimpleProduct>() {
            override fun areItemsTheSame(oldItem: SimpleProduct, newItem: SimpleProduct)
                    = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SimpleProduct, newItem: SimpleProduct)
                    = oldItem == newItem
        }
        const val VIEW_TYPE_STORE = 0
        const val VIEW_TYPE_PRODUCT = 1
        const val VIEW_TYPE_LOADING = 2

        private const val OFFSET = 1
    }
}