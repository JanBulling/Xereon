package com.xereon.xereon.ui.main.explore.items

import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.data.products.SimpleProduct
import com.xereon.xereon.databinding.ExploreHorizontalItemBinding
import com.xereon.xereon.ui.main.explore.ExploreAdapter
import com.xereon.xereon.util.lists.decorations.LeftRightPaddingDecorator

class HorizontalProducts(parent: ViewGroup) : ExploreAdapter.ExploreItemVH<HorizontalProducts.Item, ExploreHorizontalItemBinding>(
    R.layout.explore_horizontal_item, parent
) {

    private val productAdapter by lazy { HorizontalProductsAdapter() }

    override val viewBinding = lazy {
        ExploreHorizontalItemBinding.bind(itemView).apply {
            recycler.apply {
                setHasFixedSize(true)
                adapter = productAdapter
                addItemDecoration(LeftRightPaddingDecorator(startPadding = R.dimen.spacing_mega_tiny))
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                itemAnimator = null
            }
        }
    }

    override val onBindData: ExploreHorizontalItemBinding.(
        item: Item,
        payloads: List<Any>
    ) -> Unit = { item, _ ->
        title.setText(item.title)
        subtitle.setText(item.subtitle)
        productAdapter.update(item.data)
        productAdapter.setOnProductClickListener(item.onProductClickAction)
    }

    data class Item(
        val data: List<SimpleProduct>,
        val onProductClickAction: (SimpleProduct) -> Unit,
        @StringRes val title: Int,
        @StringRes val subtitle: Int
    ) : ExploreItem {
        override val stableId: Long = (Item::class.java.name.hashCode() + title + subtitle).toLong()

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