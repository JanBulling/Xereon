package com.xereon.xereon.data.explore

import android.graphics.Color

data class ExploreBannerData(
    val title: String = "",
    val backgroundColorString: String = "#252525",
    val fontColorString: String = "#F0F0F0",
) {
    val isDataAvailable: Boolean = title.isNotEmpty()

    val fontColor: Int
        get() = Color.parseColor(fontColorString)

    val backgroundColor: Int
        get() = Color.parseColor(backgroundColorString)

    override fun toString(): String {
        return "ExploreBannerData(text=$title, color=$fontColor, background=$backgroundColor)"
    }
}