package com.xereon.xereon.adapter.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.xereon.xereon.R
import com.xereon.xereon.data.model.places.Place

class PlacesAdapter() : RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    private lateinit var itemClickListener: ItemClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_places_autocomplete, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() =
        differ.currentList.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }


    private val diffCallback = object: DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place) =
            oldItem.hashCode() == newItem.hashCode()

        override fun areContentsTheSame(oldItem: Place, newItem: Place) =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    fun submitList(list: List<Place>) {
        differ.submitList(list)
    }


    fun setOnItemClickListener(clickListener: ItemClickListener) {
        itemClickListener = clickListener
    }
    interface ItemClickListener{
        fun onItemClick(place: Place)
    }


    inner class ViewHolder(
        val view: View
    ) : RecyclerView.ViewHolder(view) {

        init {
            view.setOnClickListener {
                val currentIndex = bindingAdapterPosition
                if (currentIndex != RecyclerView.NO_POSITION) {
                    val place = differ.currentList[currentIndex]
                    if (place != null)
                        itemClickListener.onItemClick(place)
                }
            }
        }

        fun bind(place: Place) {
            val city = if(place.city.isNullOrEmpty()) "" else "${place.city[0]}, "
            val region = "$city${place.mainAdministrative}"

            val locationNameTV = view.findViewById<TextView>(R.id.location_suggestion_name)
            val regionTV = view.findViewById<TextView>(R.id.location_suggestion_region)
            val locationImage = view.findViewById<ImageView>(R.id.location_suggestion_img)

            locationNameTV.text = place.locationName
            regionTV.text = region
            locationImage.setImageResource(if(place.isCity) R.drawable.ic_city else R.drawable.ic_location)
        }
    }
}