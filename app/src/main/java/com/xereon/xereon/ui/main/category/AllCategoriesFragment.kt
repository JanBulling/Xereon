package com.xereon.xereon.ui.main.category

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.adapter.recyclerAdapter.CategoryVerticalAdapter
import com.xereon.xereon.data.category.source.CategoryProvider
import com.xereon.xereon.databinding.FragmentAllCategoriesBinding
import com.xereon.xereon.ui.main.MainActivity
import com.xereon.xereon.util.lists.decorations.TopBottomPaddingDecorator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllCategoriesFragment : Fragment(R.layout.fragment_all_categories) {
    companion object {
        private val TAG = AllCategoriesFragment::class.simpleName
    }

    private var _binding: FragmentAllCategoriesBinding? = null
    private val binding get() = _binding!!

    private val categoriesAdapter = AllCategoryAdapter()

    @Inject lateinit var categoryProvider: CategoryProvider
    private val menu = AllCategoryMenu()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAllCategoriesBinding.bind(view)

        menu.setupSearchMenu(binding.toolbar, "Kategorie suchen",
            { categoriesAdapter.filter.filter(it) },
            { (requireActivity() as MainActivity).goBack() }
        )

        categoriesAdapter.update(categoryProvider.getAllCategories())
        categoriesAdapter.setOnCategoryClickListener {
            Toast.makeText(requireContext(), it.toString(), Toast.LENGTH_SHORT).show()
        }

        binding.recycler.apply {
            layoutManager = LinearLayoutManager(requireContext())
            itemAnimator = null
            addItemDecoration(TopBottomPaddingDecorator(topPadding = R.dimen.spacing_ultra_tiny))
            adapter = categoriesAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}