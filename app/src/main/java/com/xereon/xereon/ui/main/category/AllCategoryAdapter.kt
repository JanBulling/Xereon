package com.xereon.xereon.ui.main.category

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.data.category.Categories
import com.xereon.xereon.data.category.Category
import com.xereon.xereon.databinding.CategoryItemBinding
import java.util.*
import kotlin.collections.ArrayList

class AllCategoryAdapter : RecyclerView.Adapter<AllCategoryAdapter.VH>(), Filterable {

    private lateinit var onClickAction: (Categories) -> Unit
    private val completeList = arrayListOf<Category>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            CategoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun getItemCount() : Int = differ.currentList.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.bind(differ.currentList[position])
    }



    private val diffCallback = object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category) =
            oldItem.category == newItem.category

        override fun areContentsTheSame(oldItem: Category, newItem: Category) =
            oldItem.hashCode() == newItem.hashCode()
    }
    private val differ = AsyncListDiffer(this, diffCallback)
    fun update(list: List<Category>){
        completeList.clear()
        completeList.addAll(list)
        differ.submitList(list)
    }



    fun setOnCategoryClickListener(onClickAction: (Categories) -> Unit) {
        this.onClickAction = onClickAction
    }



    inner class VH(private val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            binding.apply {

                categoryName.text = category.categoryName
                if (category.iconId != -1)
                    categoryIcon.setImageResource(category.iconId)
                categoryIcon.backgroundTintList = root.context.getColorStateList(category.colorId)

                root.setOnClickListener {
                    if (::onClickAction.isInitialized)
                        onClickAction.invoke(category.category)
                }
            }
        }
    }

    /*
    * Filter for filtering the categories
    * returns all results where the name OR one name of a sub-category equals
    * the input string
    *
    * O(n·m)
    *
    */
    override fun getFilter(): Filter {
        return object : Filter() {
            private val filterResults = FilterResults()
            override fun performFiltering(query: CharSequence?): FilterResults {

                val results = arrayListOf<Category>()
                val filterQuery = query.toString().toLowerCase(Locale.US)

                for (category: Category in completeList) {
                    if (category.categoryName.toLowerCase(Locale.US).contains(filterQuery)) {
                        results.add(category)
                    } else {
                        for (subCategory: String in category.subCategories) {
                            if (subCategory.toLowerCase(Locale.US).contains(filterQuery)) {
                                results.add(category)
                                break
                            }
                        }
                    }
                }
                return filterResults.also { it.values = results }
            }

            override fun publishResults(query: CharSequence?, results: FilterResults?) {
                if (results != null && results.values is ArrayList<*>)
                    differ.submitList(results.values as ArrayList<Category>)
            }

        }
    }
}