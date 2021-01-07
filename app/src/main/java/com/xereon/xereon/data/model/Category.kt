package com.xereon.xereon.data.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes

data class Category(
    val categoryIndex: Int,
    val categoryName: String,
    @ColorRes val colorId: Int,
    @DrawableRes val iconId: Int,
    @DrawableRes val headerImgId: Int,
    val categoryDescription: String,
    val subCategories: Array<String>
) {
}