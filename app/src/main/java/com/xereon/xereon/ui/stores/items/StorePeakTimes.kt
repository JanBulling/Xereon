package com.xereon.xereon.ui.stores.items

import android.view.ViewGroup
import com.xereon.xereon.R
import com.xereon.xereon.data.model.places.GooglePlacesData
import com.xereon.xereon.databinding.StorePeakTimesBinding
import com.xereon.xereon.ui.stores.items.PeakTimesAdapter
import com.xereon.xereon.ui.stores.StoreAdapter
import java.util.*

class StorePeakTimes(parent: ViewGroup) :
    StoreAdapter.StoreItemVH<StorePeakTimes.Item, StorePeakTimesBinding>(
        R.layout.store_peak_times, parent
    ) {

    override val viewBinding = lazy {
        StorePeakTimesBinding.bind(itemView)
    }

    override val onBindData: StorePeakTimesBinding.(
        item: Item,
        payloads: List<Any>
    ) -> Unit = { item, _ ->
        val currentPopularity = item.data.currentPopularity
        val popularTimes = item.data.popularTimes

        val calendar = Calendar.getInstance()
        var currentDay: Int = (calendar.get(Calendar.DAY_OF_WEEK) + 5) % 7
        val today = currentDay
        val currentHour: Int = calendar.get(Calendar.HOUR_OF_DAY) -1
        val peakTimesAdapter = PeakTimesAdapter()

        if (popularTimes[currentDay][currentHour] == 0)
            peaktimesInformation.text = "Geschlossen"
        else if (popularTimes[currentDay][currentHour] - 5 > currentPopularity)
            peaktimesInformation.text = "Wenig besucht"
        else if (popularTimes[currentDay][currentHour] + 5 < currentPopularity)
            peaktimesInformation.text = "Stark besucht"
        else
            peaktimesInformation.text = "So viele Besucher wie gewöhnlich"

        peakTimesAdapter.submitData(popularTimes[currentDay], currentPopularity, currentHour)
        peaktimesChart.adapter = peakTimesAdapter

        peaktimesNext.setOnClickListener{
            if (currentDay != 6) currentDay++ else currentDay = 0
            if (currentDay == today)
                peakTimesAdapter.submitData(popularTimes[currentDay], currentPopularity, currentHour)
            else
                peakTimesAdapter.submitData(popularTimes[currentDay])
            peaktimesCurrentDay.text = resources.getStringArray(R.array.day_of_week)[currentDay]
        }
        peaktimesPrevious.setOnClickListener{
            if (currentDay != 0) currentDay-- else currentDay = 6
            if (currentDay == today)
                peakTimesAdapter.submitData(popularTimes[currentDay], currentPopularity, currentHour)
            else
                peakTimesAdapter.submitData(popularTimes[currentDay])
            peaktimesCurrentDay.text = resources.getStringArray(R.array.day_of_week)[currentDay]
        }
        peaktimesCurrentDay.text = resources.getStringArray(R.array.day_of_week)[currentDay]
    }

    data class Item(
        val data: GooglePlacesData
    ) : StoreItem {
        override val stableId: Long = Item::class.java.name.hashCode().toLong()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Item

            if (data != other.data) return false
            if (stableId != other.stableId) return false

            return true
        }

        override fun hashCode(): Int {
            return data.hashCode() * 31 + stableId.hashCode()
        }
    }
}