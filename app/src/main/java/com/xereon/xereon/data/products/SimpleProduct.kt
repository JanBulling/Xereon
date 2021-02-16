package com.xereon.xereon.data.products

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SimpleProduct(
    val id : Int,
    val name : String,
    val price : String,
    val unit : Int,
    val appoffer : Boolean
): Parcelable {
    val productImageURL get() = "http://vordertuer.bplaced.net/app-img/products/" + id + ".png"
}