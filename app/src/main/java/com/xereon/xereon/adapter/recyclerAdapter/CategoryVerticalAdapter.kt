package com.xereon.xereon.adapter.recyclerAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.data.model.Category
import com.xereon.xereon.databinding.RecyclerCategoryVerticalBinding
import android.widget.Filter
import java.util.*
import kotlin.ConcurrentModificationException

/* filterable list adapter  */
class CategoryVerticalAdapter : RecyclerView.Adapter<CategoryVerticalAdapter.ViewHolder>(), Filterable {

    private lateinit var completeList: List<Category>
    private lateinit var itemClickListener: ItemClickListener

    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerCategoryVerticalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount() =
        differ.currentList.size


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private val diffCallback = object: DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) =
            oldItem.categoryIndex == newItem.categoryIndex

        override fun areContentsTheSame(oldItem: Category, newItem: Category) =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun submitList(list: List<Category>) {
        if (!::completeList.isInitialized)
            completeList = list

        differ.submitList(list)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(clickListener: ItemClickListener) { itemClickListener = clickListener }
    interface ItemClickListener{ fun onItemClick(category:Category) }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    /* Filter for filtering the categories
    * returns all results where the name OR one name of a sub-category equals
    * the input string
    *
    * O(n²)
    *
    */
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


    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(private val binding: RecyclerCategoryVerticalBinding)
        : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (::itemClickListener.isInitialized) {
                    val currentIndex = bindingAdapterPosition
                    if (currentIndex != RecyclerView.NO_POSITION) {
                        val currentCategory = differ.currentList[currentIndex]
                        if (currentCategory != null)
                            itemClickListener.onItemClick(currentCategory)
                    }
                }
            }
        }

        fun bind(category: Category) {
            binding.category = category
            binding.executePendingBindings()
        }
    }
}