package com.xereon.xereon.ui.main.category

import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.xereon.xereon.R

class AllCategoryMenu {

    fun setupSearchMenu(
        toolbar: Toolbar,
        queryHint: String,
        onTextChanged: (String) -> Unit,
        onNavigateClicked: () -> Unit
    ) = toolbar.apply {
        inflateMenu(R.menu.menu_all_categories)
        val searchView = menu.findItem(R.id.menu_item_search_category).actionView as SearchView

        searchView.queryHint = queryHint
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true

            override fun onQueryTextChange(query: String?): Boolean {
                onTextChanged.invoke(query.orEmpty())
                return true
            }
        })

        setNavigationOnClickListener {
            onNavigateClicked.invoke()
        }
    }

}