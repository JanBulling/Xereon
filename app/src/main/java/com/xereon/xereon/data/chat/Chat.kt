package com.xereon.xereon.data.chat

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class Chat(
    @SerializedName("partner") val storeID: Int,
    @SerializedName("storename") val storeName: String,
    @SerializedName("unread") val unreadMessages: Int,
    @SerializedName("lastmessage") val lastMessageTime: Long,
) {
    val logoImageURL get() = "http://vordertuer.bplaced.net/app-img/stores/logo/$storeID.png"
    val timeFromLastMessage
        get() = SimpleDateFormat(
            "dd.MM.yyyy,  HH:mm",
            Locale.getDefault()
        ).format(Date(lastMessageTime * 1000L))
}