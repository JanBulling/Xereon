package com.xereon.xereon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.databinding.RecyclerProductHorizontalBinding

class ProductHorizontalAdapter(
    private val mListener: OnClickListener
) : RecyclerView.Adapter<ProductHorizontalAdapter.ViewHolder>() {

    var mList: List<SimpleProduct>? = null;

    fun setList(list: List<SimpleProduct>) {
        if (mList == null) {
            mList = list
            notifyItemRangeInserted(0, list.size)
        } else {
            val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                override fun getOldListSize() = mList!!.size

                override fun getNewListSize() = list.size

                override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int)
                        = mList!!.get(oldItemPosition).id == list[newItemPosition].id

                override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int)
                        = oldItemPosition == newItemPosition
            })
            mList = list
            result.dispatchUpdatesTo(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerProductHorizontalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.setClickListener(mListener)

        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        if (mList == null)
            return 0

        return if (mList!!.size > 10) MAX_LIST_SIZE  else  mList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.product = mList!![position]
        holder.binding.executePendingBindings()
    }

    interface OnClickListener {
        fun onClick(store: SimpleProduct)
    }


    inner class ViewHolder(
        val binding: RecyclerProductHorizontalBinding
    ) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val MAX_LIST_SIZE = 10
    }
}