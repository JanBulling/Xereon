package com.xereon.xereon.ui.products.items

import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.xereon.xereon.R
import com.xereon.xereon.data.products.Product
import com.xereon.xereon.databinding.ProductMoreFromStoreBinding
import com.xereon.xereon.ui.products.ProductAdapter
import com.xereon.xereon.util.lists.decorations.LeftRightPaddingDecorator

class MoreProductsFromStore(parent: ViewGroup) :
    ProductAdapter.ProductItemVH<MoreProductsFromStore.Item, ProductMoreFromStoreBinding>(
        R.layout.product_more_from_store, parent
    ) {

    private val productsAdapter by lazy { ProductVerticalAdapter() }

    override val viewBinding = lazy {
        ProductMoreFromStoreBinding.bind(itemView).apply {
            recycler.apply {
                setHasFixedSize(false)
                itemAnimator = null
                addItemDecoration(LeftRightPaddingDecorator(R.dimen.spacing_ultra_tiny))
                layoutManager = GridLayoutManager(context, 2)
                adapter = productsAdapter
            }
        }
    }

    override val onBindData: ProductMoreFromStoreBinding.(
        item: Item,
        payloads: List<Any>
    ) -> Unit = { item, _ ->
        with(item.data) {
            Glide.with(context).load(officeImageURL).into(storeImg)
            storeNameTxt.text = storeName
            productsAdapter.update(otherStoreProducts)
            productsAdapter.setOnProductClickListener {
                item.actionOnClick.invoke(it.id, it.name)
            }
            storeImg.setOnClickListener {
                item.actionToStore.invoke(storeID, storeName)
            }
        }
    }

    data class Item(
        val data: Product,
        val actionToStore: (Int, String) -> Unit,
        val actionOnClick: (Int, String) -> Unit
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