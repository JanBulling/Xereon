package com.xereon.xereon.adapter.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.R
import com.xereon.xereon.data.maps.Place
import com.xereon.xereon.databinding.RecyclerPlacesAutocompleteBinding

class PlacesAdapter() : RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerPlacesAutocompleteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() =
        differ.currentList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    private val diffCallback = object: DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place) =
            oldItem.name == newItem.name

        override fun areContentsTheSame(oldItem: Place, newItem: Place) =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Place>) {
        differ.submitList(list)
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    fun setOnItemClickListener(clickListener: ItemClickListener) { itemClickListener = clickListener }
    interface ItemClickListener{ fun onItemClick(place: Place) }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    inner class ViewHolder(
        val binding: RecyclerPlacesAutocompleteBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (::itemClickListener.isInitialized) {
                    val currentIndex = bindingAdapterPosition
                    if (currentIndex != RecyclerView.NO_POSITION) {
                        val currentItem = differ.currentList[currentIndex]
                        if (currentItem != null)
                            itemClickListener.onItemClick(currentItem)
                    }
                }
            }
        }

        fun bind(place: Place) {
            val region = "${place.city}${place.administrative}"

            binding.apply {
                locationSuggestionName.text = place.name
                locationSuggestionRegion.text = region
                locationSuggestionImg.setImageResource(if(place.isCity) R.drawable.ic_city else R.drawable.ic_location)
            }
        }
    }
}