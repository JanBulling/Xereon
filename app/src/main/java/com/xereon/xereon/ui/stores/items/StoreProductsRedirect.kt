package com.xereon.xereon.ui.stores.items

import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.data.products.SimpleProduct
import com.xereon.xereon.data.store.SimpleStore
import com.xereon.xereon.databinding.StoreOpeningHoursBinding
import com.xereon.xereon.databinding.StoreProductsRedirectBinding
import com.xereon.xereon.ui.main.explore.items.HorizontalProductsAdapter
import com.xereon.xereon.ui.main.explore.items.HorizontalStoresAdapter
import com.xereon.xereon.ui.stores.StoreAdapter
import com.xereon.xereon.util.lists.decorations.LeftRightPaddingDecorator

class StoreProductsRedirect(parent: ViewGroup) :
    StoreAdapter.StoreItemVH<StoreProductsRedirect.Item, StoreProductsRedirectBinding>(
        R.layout.store_products_redirect, parent
    ) {

    private val productAdapter by lazy { HorizontalProductsAdapter() }

    override val viewBinding = lazy {
        StoreProductsRedirectBinding.bind(itemView).apply {
            productsRecycler.apply {
                setHasFixedSize(true)
                adapter = productAdapter
                addItemDecoration(LeftRightPaddingDecorator(startPadding = R.dimen.spacing_mega_tiny))
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                itemAnimator = null
            }
        }
    }

    override val onBindData: StoreProductsRedirectBinding.(
        item: Item,
        payloads: List<Any>
    ) -> Unit = { item, _ ->
        productsBtn.setOnClickListener { item.redirect.invoke() }
        if (item.data.isEmpty())
            productsRecycler.isVisible = false
        productAdapter.update(item.data)
        productAdapter.setOnProductClickListener(item.onProductClickAction)
    }

    data class Item(
        val data: List<SimpleProduct>,
        val redirect: () -> Unit,
        val onProductClickAction: (SimpleProduct) -> Unit,
        ) : StoreItem {
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