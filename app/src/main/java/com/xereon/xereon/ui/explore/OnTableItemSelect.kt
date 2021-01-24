package com.xereon.xereon.ui.explore

import com.xereon.xereon.data.util.CategoryUtils

interface OnTableItemSelect {
    fun onTableSelect(category: CategoryUtils.Categories)
}