package com.xereon.xereon.ui.main.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.databinding.SubCategoryItemBinding

class SubCategoriesAdapter : RecyclerView.Adapter<SubCategoriesAdapter.VH>() {

    private lateinit var onClickAction: (String) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = SubCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(differ.currentList[position])
    }


    fun setOnSubCategoryClickListener(onClickAction: (String) -> Unit) {
        this.onClickAction = onClickAction
    }


    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String) =
            oldItem == newItem

        override fun areContentsTheSame(oldItem: String, newItem: String) =
            oldItem.hashCode() == newItem.hashCode()
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun update(array: Array<String>) = differ.submitList(array.toList())


    inner class VH(val binding: SubCategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (::onClickAction.isInitialized) {
                    val index = bindingAdapterPosition
                    if (index != RecyclerView.NO_POSITION) {
                        val item = differ.currentList[index]
                        onClickAction.invoke(item)
                    }
                }
            }
        }

        fun bind(subCategory: String) {
            binding.apply {
                subCategoryName.text = subCategory
            }
        }
    }

}