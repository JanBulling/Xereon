package com.xereon.xereon.data.model

data class ExploreData(
    val chatNewMessages : Boolean = false,
    val update : Boolean = false,
    val newStores : List<SimpleStore>,
    val recommendations : List<SimpleProduct>,
    val popular : List<SimpleProduct>
) {
    override fun toString(): String {
        return "Has chat messages: $chatNewMessages, newStores: ${newStores.size}-elements, " +
                "recommended: ${recommendations.size}-elements, popular: ${popular.size}-elements"
    }
}