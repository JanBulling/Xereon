package com.xereon.xereon.ui.main.explore.items

import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.data.category.PopularCategory
import com.xereon.xereon.databinding.ExplorePopularCategoriesBinding
import com.xereon.xereon.ui.main.explore.ExploreAdapter
import com.xereon.xereon.util.lists.decorations.PopularCategoriesPaddingDecorator

class PopularCategories(parent: ViewGroup) : ExploreAdapter.ExploreItemVH<PopularCategories.Item, ExplorePopularCategoriesBinding>(
    R.layout.explore_popular_categories, parent
) {

    private val categoryAdapter by lazy { PopularCategoriesAdapter() }

    override val viewBinding = lazy {
        ExplorePopularCategoriesBinding.bind(itemView).apply {
            recyclerPopularCategories.apply {
                setHasFixedSize(false)
                adapter = categoryAdapter
                addItemDecoration(PopularCategoriesPaddingDecorator(
                    startPadding = R.dimen.spacing_mega_tiny,
                    endPadding = R.dimen.spacing_mega_tiny,
                    verticalPadding = R.dimen.spacing_ultra_tiny,
                    distanceBetween = R.dimen.spacing_mega_tiny
                ))
                layoutManager = GridLayoutManager(context, 2)
                itemAnimator = null
            }
        }
    }

    override val onBindData: ExplorePopularCategoriesBinding.(
        item: Item,
        payloads: List<Any>
    ) -> Unit = { item, _ ->
        categoryAdapter.update(item.data)
        seeMoreCategories.setOnClickListener {
            item.seeMoreCategoriesAction.invoke()
        }
    }

    data class Item(
        val data: List<PopularCategory>,
        val seeMoreCategoriesAction: () -> Unit
        ) : ExploreItem {
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