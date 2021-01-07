package com.xereon.xereon.ui.categories

import android.os.Bundle
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.databinding.adapters.SearchViewBindingAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.CategoryVerticalAdapter
import com.xereon.xereon.data.util.CategoryUtils
import com.xereon.xereon.ui.MainActivity
import kotlinx.android.synthetic.main.frg_categories.*

class AllCategoriesFragment : Fragment(R.layout.frg_categories) {

    private lateinit var categoryAdapter: CategoryVerticalAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (activity as MainActivity).setBottomNavBarVisibility(false)

        categoryAdapter = CategoryVerticalAdapter()
        categoryAdapter.submitList(CategoryUtils.getAllCategories().toList())
        all_categories_list.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(requireContext())
            adapter = categoryAdapter
        }

        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_search, menu)

        val searchItem = menu.findItem(R.id.menu_item_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = "Kategorie suchen"
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true

            override fun onQueryTextChange(query: String?): Boolean {
                categoryAdapter.filter.filter(query)
                return true
            }
        })
    }
}