package com.xereon.xereon.ui.stores.items

import android.graphics.Typeface
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.AlignmentSpan
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.xereon.xereon.R
import com.xereon.xereon.databinding.StoreOpeningHoursBinding
import com.xereon.xereon.ui.stores.StoreAdapter
import com.xereon.xereon.util.view_utils.setTextCategoryColor
import kotlinx.coroutines.currentCoroutineContext
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.util.*

class StoreOpeningHour(parent: ViewGroup) :
    StoreAdapter.StoreItemVH<StoreOpeningHour.Item, StoreOpeningHoursBinding>(
        R.layout.store_opening_hours, parent
    ) {

    override val viewBinding = lazy {
        StoreOpeningHoursBinding.bind(itemView)
    }

    override val onBindData: StoreOpeningHoursBinding.(
        item: Item,
        payloads: List<Any>
    ) -> Unit = { item, _ ->
        val dayOfWeek = (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) + 5) % 7
        for (i in (0..6)) {
            val textView = openingHoursTimes.getChildAt(i) as TextView

            val openingTime = item.data[i].ifEmpty { "geschlossen" }
            textView.text = openingTime

            if (dayOfWeek == i) {
                val dayTextView = openingHoursDay.getChildAt(i) as TextView
                dayTextView.setTextColor( context.getColor(R.color.primary) )
                textView.setTextColor( context.getColor(R.color.primary) )
                textView.setTypeface(null, Typeface.BOLD)

                if (isOpened(openingTime))
                    openingHoursCurrent.text = "Aktuell geöffnet"
            }
        }
    }

    private fun isOpened(openingHours: String): Boolean {
        val sdf = SimpleDateFormat("HH:mm", Locale.GERMANY)
        val openingTime = openingHours.split("-")
        val currentTime = sdf.parse(sdf.format(Date())) ?: Date(0)
        val open = sdf.parse(openingTime[0]) ?: Date(0)
        val close = sdf.parse(openingTime[1]) ?: Date(0)
        return currentTime.after(open) && currentTime.before(close)
    }

    data class Item(
        val data: Array<String>
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