package com.xereon.xereon.data.model

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val categoryIndex: Int,
    val categoryName: String,
    @ColorRes val colorId: Int,
    @DrawableRes val iconId: Int,
    @DrawableRes val headerImgId: Int,
    val categoryDescription: String,
    val subCategories: Array<String>
): Parcelable {
}