package com.xereon.xereon.data.model

import com.google.gson.annotations.SerializedName

data class ChatMessage(
    val id: Long,
    @SerializedName("content") val message: String,
    @SerializedName("sent") val fromAppUser: Boolean,
    @SerializedName("date") val date: Long,
) {

    override fun toString() = "$message, $fromAppUser"

}