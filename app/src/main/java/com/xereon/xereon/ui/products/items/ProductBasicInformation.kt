package com.xereon.xereon.ui.products.items

import android.text.Html
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.xereon.xereon.R
import com.xereon.xereon.data.products.Product
import com.xereon.xereon.data.util.PriceUtils
import com.xereon.xereon.databinding.ProductBasicInformationBinding
import com.xereon.xereon.databinding.StoreBasicInformationBinding
import com.xereon.xereon.ui.products.ProductAdapter

class ProductBasicInformation(parent: ViewGroup) :
    ProductAdapter.ProductItemVH<ProductBasicInformation.Item, ProductBasicInformationBinding>(
        R.layout.product_basic_information, parent
    ) {

    override val viewBinding = lazy {
        ProductBasicInformationBinding.bind(itemView)
    }

    override val onBindData: ProductBasicInformationBinding.(
        item: Item,
        payloads: List<Any>
    ) -> Unit = { item, _ ->
        with(item.data) {
            Glide.with(context).load(productImageURL).into(productImage)
            productName.text = name
            productAppExclusive.isVisible = appoffer
            productDescription.text = Html.fromHtml(description)
            productPrice.text = PriceUtils.getPriceWithUnitAsString(price, unit)
        }
    }

    data class Item(
        val data: Product
    ) : ProductItem {
        override val stableId: Long = Item::class.java.name.hashCode().toLong()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Item

            if (data != other.data) return false
            if (stableId != other.stableId) return false

            return true
        }

        override fun hashCode(): Int {
            return data.hashCode() * 31 + stableId.hashCode()
        }
    }

}