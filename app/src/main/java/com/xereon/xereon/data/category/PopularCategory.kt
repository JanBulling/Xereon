package com.xereon.xereon.data.category

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class PopularCategory(
    val category: Categories,
    @StringRes val name: Int,
    @DrawableRes val iconResource: Int,
    val onClick: (Categories) -> Unit = {}
)