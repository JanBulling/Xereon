package com.xereon.xereon.adapter.recyclerAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.SimpleStore
import com.xereon.xereon.databinding.RecyclerProductHorizontalBinding

class ProductHorizontalAdapter() : RecyclerView.Adapter<ProductHorizontalAdapter.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener
    private lateinit var mList: List<SimpleProduct>


    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerProductHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = mList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        if (::mList.isInitialized) {
            return if (mList.size > MAX_LIST_SIZE)
                MAX_LIST_SIZE
            else
                mList.size
        }
        return 0
    }



    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun submitList(list: List<SimpleProduct>) {
        if (!::mList.isInitialized) {
            mList = list
            notifyItemRangeInserted(0, list.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = mList.size
                override fun getNewListSize() = list.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
                        = mList[oldItemPosition].id == list[newItemPosition].id

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
                        = mList[oldItemPosition].hashCode() == mList[newItemPosition].hashCode()
            })
            mList = list
            result.dispatchUpdatesTo(this)
        }
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(clickListener: ItemClickListener) { itemClickListener = clickListener }
    interface ItemClickListener{ fun onItemClick(simpleProduct: SimpleProduct) }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder( val binding: RecyclerProductHorizontalBinding)
        : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val currentIndex = bindingAdapterPosition
                if (currentIndex != RecyclerView.NO_POSITION) {
                    itemClickListener.onItemClick(mList[currentIndex])
                }
            }
        }

        fun bind(simpleProduct: SimpleProduct) {
            binding.product = simpleProduct
            binding.executePendingBindings()
        }
    }

    companion object {
        const val MAX_LIST_SIZE = 10
    }
}