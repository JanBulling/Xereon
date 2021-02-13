package com.xereon.xereon.ui.main.explore.items

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.data.category.PopularCategory
import com.xereon.xereon.databinding.ExploreCategoryItemBinding

class PopularCategoriesAdapter : RecyclerView.Adapter<PopularCategoriesAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ExploreCategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(differ.currentList[position])
    }



    private val diffCallback = object: DiffUtil.ItemCallback<PopularCategory>() {
        override fun areItemsTheSame(oldItem: PopularCategory, newItem: PopularCategory) =
            oldItem.category.id == newItem.category.id

        override fun areContentsTheSame(oldItem: PopularCategory, newItem: PopularCategory) =
            oldItem.hashCode() == newItem.hashCode()
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun update(list: List<PopularCategory>) = differ.submitList(list)



    inner class VH(private val binding: ExploreCategoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: PopularCategory) {
            binding.categoryText.apply {
                setText(category.name)
                setCompoundDrawablesWithIntrinsicBounds(category.iconResource, 0, 0, 0)
            }
            binding.root.setOnClickListener {
                category.onClick.invoke(category.category)
            }
        }
    }
}