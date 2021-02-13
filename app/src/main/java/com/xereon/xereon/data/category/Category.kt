package com.xereon.xereon.data.category

import android.os.Parcelable
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Category(
    val category: Categories,
    val categoryName: String,
    @ColorRes val colorId: Int,
    @DrawableRes val iconId: Int,
    @DrawableRes val headerImgId: Int,
    @StringRes val categoryDescription: Int,
    val subCategories: Array<String>
): Parcelable {
}