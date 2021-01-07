package com.xereon.xereon.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.data.model.Category
import com.xereon.xereon.databinding.RecyclerCategoryVerticalBinding
import android.widget.Filter

class CategoryVerticalAdapter : RecyclerView.Adapter<CategoryVerticalAdapter.ViewHolder>(), Filterable {

    private lateinit var completeList: List<Category>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerCategoryVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() =
        differ.currentList.size


    private val diffCallback = object: DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) =
            oldItem.categoryIndex == newItem.categoryIndex

        override fun areContentsTheSame(oldItem: Category, newItem: Category) =
            oldItem.hashCode() == newItem.hashCode()

    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list: List<Category>) {
        if (!::completeList.isInitialized)
            completeList = list

        differ.submitList(list)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            private val filterResults = FilterResults()
            override fun performFiltering(query: CharSequence?): FilterResults {

                val results = mutableListOf<Category>()
                val filterQuery = query.toString().toLowerCase()

                for (category: Category in completeList) {
                    if (category.categoryName.toLowerCase().contains(filterQuery)) {
                        results.add(category)
                    } else {
                        for (subCategory: String in category.subCategories) {
                            if (subCategory.toLowerCase().contains(filterQuery)) {
                                results.add(category)
                                break
                            }
                        }
                    }
                }
                return filterResults.also { it.values = results }
            }

            override fun publishResults(query: CharSequence?, results: FilterResults?) {
                if (results != null && results.values is MutableList<*>)
                    differ.submitList(results.values as MutableList<Category>)
            }

        }
    }

    inner class ViewHolder(private val binding: RecyclerCategoryVerticalBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.category = category
        }

    }
}