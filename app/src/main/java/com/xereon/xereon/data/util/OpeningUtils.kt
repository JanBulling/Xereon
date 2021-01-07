package com.xereon.xereon.data.util

import android.content.Context
import com.xereon.xereon.R
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

object OpeningUtils {

    fun getOpeningTimesToday(context: Context, openingString: String) : String {
        val opening = openingString.split(",")
        val dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        val index = if(dayOfWeek - 2 < 0) 6   else dayOfWeek - 2
        var openingTime = "geschlossen"
        try {
            openingTime = if (opening[index].isNullOrEmpty()) "geschlossen" else opening[index]
        } catch (e: IndexOutOfBoundsException) { /*NO-OP*/ }

        val preIndex = context.resources.getStringArray(R.array.day_of_week_short)[index]

        return "$preIndex.: $openingTime"
    }

    fun getOpeningTimes(openingString: String) : Array<String> {
        val opening = openingString.split(",").toMutableList()

        for (i in 0..6) {
            try {
                if (opening[i].isNullOrEmpty())
                    opening[i] = "geschlossen"
            } catch (e: IndexOutOfBoundsException) {
                opening.add("geschlossen")
            }
        }

        return opening.toTypedArray()
    }
}