package com.xereon.xereon.util

import android.content.Context
import android.content.res.Resources
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import kotlin.jvm.Throws

object ContextExtensions {

    fun Context.getColorCompat(@ColorRes id: Int) = ContextCompat.getColor(this, id)

    fun Context.getColorStateListCompat(@ColorRes id: Int) = ContextCompat.getColorStateList(this, id)

    fun Context.getDrawableCompat(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)

}

@Throws(Resources.NotFoundException::class)
fun Resources.getDrawableCompat(@DrawableRes id: Int, theme: Resources.Theme? = null) =
    ResourcesCompat.getDrawable(this, id, theme)
