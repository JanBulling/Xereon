package com.xereon.xereon.adapter.pagingAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.databinding.RecyclerStoreVerticalBinding

class StoresPagingAdapter() :
    PagingDataAdapter<SimpleStore, StoresPagingAdapter.ViewHolder>(COMPARATOR) {

    private lateinit var itemClickListener: ItemClickListener


    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerStoreVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val simpleStore = getItem(position)

        if (simpleStore != null)
            holder.bind(simpleStore)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun withCustomLoadStateFooter(
        footer: LoadStateAdapter<*>
    ) : ConcatAdapter {
        addLoadStateListener { loadStates ->
            if (loadStates.source.refresh is LoadState.NotLoading && itemCount == 0) {
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
    interface ItemClickListener{ fun onItemClick(simpleStore: SimpleStore) }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(private val binding: RecyclerStoreVerticalBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val currentIndex = bindingAdapterPosition
                if (currentIndex != RecyclerView.NO_POSITION) {
                    val item = getItem(currentIndex)
                    if (item != null)
                        itemClickListener.onItemClick(item)
                }
            }
        }

        fun bind(simpleStore: SimpleStore) {
            binding.store = simpleStore
        }

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    companion object {
        private val COMPARATOR = object: DiffUtil.ItemCallback<SimpleStore>() {
            override fun areItemsTheSame(oldItem: SimpleStore, newItem: SimpleStore) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: SimpleStore, newItem: SimpleStore) =
                oldItem.hashCode() == newItem.hashCode()
        }
    }
}