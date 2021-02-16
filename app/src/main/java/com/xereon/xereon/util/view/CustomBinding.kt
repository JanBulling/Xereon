package com.xereon.xereon.util.view

import android.graphics.Typeface
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.xereon.xereon.R
import com.xereon.xereon.data.products.Product
import com.xereon.xereon.data.util.PriceUtils
import java.util.*

//@BindingAdapter("isVisible")
fun setIsVisible(view: View, show: Boolean) {
    view.visibility = if (show) View.VISIBLE else View.GONE
}

//@BindingAdapter("imageRes")
fun setImageResource(imageView: ImageView, resId: Int) {
    if (resId != -1)
        imageView.setImageResource(resId)
    else
        imageView.setImageDrawable(null)
}

//@BindingAdapter("websiteText")
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