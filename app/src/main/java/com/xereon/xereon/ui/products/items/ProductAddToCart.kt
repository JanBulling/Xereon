package com.xereon.xereon.ui.products.items

import android.view.ViewGroup
import com.xereon.xereon.R
import com.xereon.xereon.data.products.Product
import com.xereon.xereon.data.util.PriceUtils
import com.xereon.xereon.databinding.ProductAddToCartBinding
import com.xereon.xereon.ui.products.ProductAdapter
import kotlinx.android.synthetic.main.frg_default_product.*

class ProductAddToCart(parent: ViewGroup) :
    ProductAdapter.ProductItemVH<ProductAddToCart.Item, ProductAddToCartBinding>(
        R.layout.product_add_to_cart, parent
    ) {

    override val viewBinding = lazy {
        ProductAddToCartBinding.bind(itemView)
    }

    override val onBindData: ProductAddToCartBinding.(
        item: Item,
        payloads: List<Any>
    ) -> Unit = { item, _ ->
        var count = 0
        val steps = PriceUtils.getStepsAsStringArray(item.unit)
        productNumberPicker.apply {
            minValue = 0
            maxValue = steps.size - 1
            displayedValues = steps
            wrapSelectorWheel = false
            setOnValueChangedListener { _, _, newValue ->
                count = newValue+1
                productOrderPrice.text = PriceUtils
                    .calculateTotalPriceAsString(item.price.toFloat(), item.unit,newValue+1)
            }
        }
        productOrderPrice.text = PriceUtils
            .calculateTotalPriceAsString(item.price.toFloat(), item.unit, 1)
        productUnit.text = PriceUtils.getBaseUnit(item.unit)
        productAddToCart.setOnClickListener {
            item.addToCartAction.invoke(count)
        }
    }

    data class Item(
        val price: String,
        val unit: Int,
        val addToCartAction: (Int) -> Unit
    ) : ProductItem {
        override val stableId: Long = Item::class.java.name.hashCode().toLong()

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Item

            if (price != other.price) return false
            if (stableId != other.stableId) return false

            return true
        }

        override fun hashCode(): Int {
            return price.hashCode() * 31 + stableId.hashCode()
        }
    }

}