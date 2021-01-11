package com.xereon.xereon.adapter.recyclerAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.R
import com.xereon.xereon.data.model.SimpleProduct

class SubCategoriesAdapter() : RecyclerView.Adapter<SubCategoriesAdapter.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener
    private lateinit var list: Array<String>


    ////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_sub_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() =
        list.size


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun submitList(subCategories: Array<String>) {
        list = subCategories
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(clickListener: ItemClickListener) { itemClickListener = clickListener }
    interface ItemClickListener{ fun onItemClick(subCategory: String) }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(private val view: View)
        : RecyclerView.ViewHolder(view) {

        private var textView: TextView = view.findViewById(R.id.recycler_sub_category_name)

        init {
            view.setOnClickListener {
                val currentIndex = bindingAdapterPosition
                if (currentIndex != RecyclerView.NO_POSITION) {
                    val currentCategory = list[currentIndex]
                    itemClickListener.onItemClick(currentCategory)
                }
            }
        }

        fun bind(subCategory: String) {
            textView.text = subCategory
        }
    }
}