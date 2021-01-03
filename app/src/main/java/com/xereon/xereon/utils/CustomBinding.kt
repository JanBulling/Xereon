package com.xereon.xereon.utils

import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.xereon.xereon.R

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

@BindingAdapter(value = ["price", "unit"])
fun setPriceText(view: TextView, price: String?, unit: Int) {
    val priceValue = "$price € / $unit"
    view.setText(priceValue)
}

@BindingAdapter("backgroundColor")
fun setBackgroundColor(view: View, storeCategory: Int) {
    view.setBackgroundColor( ContextCompat.getColor(view.context, R.color.type_red) )
}

@BindingAdapter("textCategoryColor")
fun setTextCategoryColor(view: TextView, storeCategory: Int) {
    view.setTextColor( ContextCompat.getColor(view.context, R.color.type_azure) )
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