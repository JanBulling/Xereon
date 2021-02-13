package com.xereon.xereon.ui.main.explore

import com.xereon.xereon.data.category.Categories
import com.xereon.xereon.data.model.SimpleProduct
import com.xereon.xereon.data.model.SimpleStore

sealed class ExploreEvents {
    object NavigateToAllCategories : ExploreEvents()

    data class NavigateToStore(val simpleStore: SimpleStore) : ExploreEvents()
    data class NavigateToProduct(val simpleProduct: SimpleProduct) : ExploreEvents()
    data class NavigateToCategory(val category: Categories) : ExploreEvents()

}