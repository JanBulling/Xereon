package com.xereon.xereon.ui.main.explore.items

import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.data.store.SimpleStore
import com.xereon.xereon.databinding.ExploreHorizontalItemBinding
import com.xereon.xereon.ui.main.explore.ExploreAdapter
import com.xereon.xereon.util.lists.decorations.LeftRightPaddingDecorator

class HorizontalStores(parent: ViewGroup) : ExploreAdapter.ExploreItemVH<HorizontalStores.Item, ExploreHorizontalItemBinding>(
    R.layout.explore_horizontal_item, parent
) {

    private val storesAdapter by lazy { HorizontalStoresAdapter() }

    override val viewBinding = lazy {
        ExploreHorizontalItemBinding.bind(itemView).apply {
            recycler.apply {
                setHasFixedSize(true)
                adapter = storesAdapter
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
        val titleString = resources.getString(item.title, item.city)
        title.text = titleString
        subtitle.setText(item.subtitle)
        storesAdapter.update(item.data)
        storesAdapter.setOnStoreClickListener(item.onStoreClickAction)
    }

    data class Item(
        val data: List<SimpleStore>,
        val onStoreClickAction: (SimpleStore) -> Unit,
        @StringRes val title: Int,
        val city: String,
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