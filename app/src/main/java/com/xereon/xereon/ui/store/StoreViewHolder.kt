package com.xereon.xereon.ui.store

import android.text.Html
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.xereon.xereon.R
import com.xereon.xereon.data.model.Store
import com.xereon.xereon.data.util.CategoryUtils
import com.xereon.xereon.databinding.InclStoreRecyclerBinding
import java.util.*

class StoreViewHolder(private val binding: InclStoreRecyclerBinding) :
    RecyclerView.ViewHolder(binding.root) {

    private val peakTimesAdapter = PeakTimesAdapter()

    fun bind(storeData: Store, itemClickListener: ProductsPagingAdapter.ItemClickListener) {
        binding.apply {
            val categoryColorId = CategoryUtils.getCategoryColorResourceId(storeData.category)
            val context = binding.root.context

            val calendar = Calendar.getInstance()
            var currentDay: Int = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
            val today = currentDay
            val currentHour: Int = calendar.get(Calendar.HOUR_OF_DAY) -1

            Glide.with(context).load(storeData.officeImageURL).into(storeHeaderImg)
            Glide.with(context).load(storeData.logoImageURL).into(storeLogo)
            storeLogo.clipToOutline = true

            storeType.text = storeData.type
            storeType.setBackgroundColor(context.getColor(categoryColorId))

            storeName.text = Html.fromHtml(storeData.name)

            storeRating.rating = storeData.rating
            storeRatingText.text = storeData.completeRating

            storeAddress.text = storeData.completeAddress
            storePhoneNumber.text = storeData.phone
            setWebsiteURL(storeData.website, storeWeb)

            storeDescription.text = storeData.description

            storeOpeningHours.currentDay = currentDay
            storeOpeningHours.opening = storeData.openinghours

            val popularTimes = storeData.placesData!!.popularTimes
            val currentPopularity = storeData.placesData!!.currentPopularity

            if (popularTimes[currentDay][currentHour] == 0)
                storePeakTimesVisited.text = "Geschlossen"
            else if (popularTimes[currentDay][currentHour] - 5 > currentPopularity)
                storePeakTimesVisited.text = "Wenig besucht"
            else if (popularTimes[currentDay][currentHour] + 5 < currentPopularity)
                storePeakTimesVisited.text = "Stark besucht"
            else
                storePeakTimesVisited.text = "So viele Besucher wie gewöhnlich"

            peakTimesAdapter.submitData(popularTimes[currentDay], currentPopularity, currentHour)
            storePeakChart.adapter = peakTimesAdapter
            storePeakTimesNext.setOnClickListener{
                if (currentDay != 6) currentDay++ else currentDay = 0
                if (currentDay == today)
                    peakTimesAdapter.submitData(popularTimes[currentDay], currentPopularity, currentHour)
                else
                    peakTimesAdapter.submitData(popularTimes[currentDay])
                storePeakTimeCurrentDay.text = context.resources.getStringArray(R.array.day_of_week)[currentDay]
            }
            storePeakTimesPrev.setOnClickListener{
                if (currentDay != 0) currentDay-- else currentDay = 6
                if (currentDay == today)
                    peakTimesAdapter.submitData(popularTimes[currentDay], currentPopularity, currentHour)
                else
                    peakTimesAdapter.submitData(popularTimes[currentDay])
                storePeakTimeCurrentDay.text = context.resources.getStringArray(R.array.day_of_week)[currentDay]
            }
            storePeakTimeCurrentDay.text = context.resources.getStringArray(R.array.day_of_week)[currentDay]

            storeSaveFavorite.setOnClickListener { itemClickListener.onAddToFavoriteClicked() }
            storeNavigate.setOnClickListener { itemClickListener.onNavigationClicked(storeData.coordinates) }
            storeChat.setOnClickListener { itemClickListener.onChatClicked() }
        }
    }

    private fun setWebsiteURL(url: String, view: TextView) {
        val linkAddress = "<a href=\"$url\">$url</a>"
        view.text = Html.fromHtml(linkAddress)
        view.movementMethod = LinkMovementMethod.getInstance()
    }
}