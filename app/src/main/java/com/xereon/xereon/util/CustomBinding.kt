package com.xereon.xereon.util

import android.graphics.Typeface
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.xereon.xereon.R
import com.xereon.xereon.data.model.Product
import com.xereon.xereon.data.util.CategoryUtils
import com.xereon.xereon.data.util.OpeningUtils
import com.xereon.xereon.data.util.PriceUtils

@BindingAdapter("isVisible")
fun setIsVisible(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter(value = ["show", "show2"])
fun setIsVisible(view: View, show: Boolean, show2: Boolean) {
    view.visibility = if (show && show2) View.VISIBLE else View.GONE
}

@BindingAdapter("imageURL")
fun setImageURL(imageView: ImageView, url: String?) {
    imageView.clipToOutline = true
    Glide.with(imageView.context)
        .load(url)
        .into(imageView)
}

@BindingAdapter("imageRes")
fun setImageResource(imageView: ImageView, resId: Int) {
    if (resId != -1)
        imageView.setImageResource(resId)
    else
        imageView.setImageDrawable(null)
}

@BindingAdapter(value = ["price", "unit"])
fun setPriceText(view: TextView, price: String?, unit: Int) {
    val priceValue = PriceUtils.getPriceWithUnitAsString(price ?: "0.00", unit)
    view.text = priceValue
}

@BindingAdapter("backgroundColor")
fun setBackgroundColor(view: View, category: Int) {
    @ColorRes val colorId = CategoryUtils.getCategoryColorId(category)
    view.backgroundTintList = view.context.getColorStateList(colorId)
}

@BindingAdapter("textCategoryColor")
fun setTextCategoryColor(view: TextView, storeCategory: Int) {
    @ColorRes val colorId = CategoryUtils.getCategoryColorId(storeCategory)
    view.setTextColor( ContextCompat.getColor(view.context, colorId) )
}

@BindingAdapter("websiteText")
fun setWebsiteText(view: TextView, websiteURL: String?) {
    if (websiteURL == null || websiteURL.length < 5) {
        view.text = "-"
        return
    } else {
        val linkAddress = if(websiteURL.contains("http")) "<a href=\"$websiteURL\">$websiteURL</a>"
                else "<a href=\"http://$websiteURL\">$websiteURL</a>"
        view.text = Html.fromHtml(linkAddress)
        view.movementMethod = LinkMovementMethod.getInstance()
    }
}

@BindingAdapter("isCurrentDay")
fun setCurrentDay(view: TextView, isCurrentDay: Boolean) {
    if (isCurrentDay) {
        view.setTextColor( ContextCompat.getColor(view.context, R.color.primary) )
        view.setTypeface(null, Typeface.BOLD)
    }
}

@BindingAdapter("todayOpening")
fun setOpeningForToday(view: TextView, openingString: String?) {
    val opening = OpeningUtils.getOpeningTimesToday(view.context, openingString ?: ",,,,,,")
    view.text = opening
}

@BindingAdapter("priceStartingValue")
fun setPriceStartingValue(view: TextView, product: Product?) {
    if (product != null) {
        val price = product.price.toFloat()
        view.text = PriceUtils.calculateTotalPrice(price, product.unit, 1)
    }
}