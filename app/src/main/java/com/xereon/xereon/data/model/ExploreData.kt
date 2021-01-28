package com.xereon.xereon.data.model

import android.graphics.Color

data class ExploreData(
    val chatNewMessages : Boolean = false,
    val update : Boolean = false,
    val title: String = "",
    val backgroundColor: String = "#ff0000",
    val fontColor: String = "#0000ff",
    val link: String = "",
    val newStores : List<SimpleStore>,
    val recommendations : List<SimpleProduct>,
    val popular : List<SimpleProduct>,
) {
    override fun toString(): String {
        return "Has chat messages: $chatNewMessages, newStores: ${newStores.size}-elements, " +
                "recommended: ${recommendations.size}-elements, popular: ${popular.size}-elements"
    }
}