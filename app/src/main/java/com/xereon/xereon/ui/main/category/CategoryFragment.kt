package com.xereon.xereon.ui.main.category

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.xereon.xereon.R
import com.xereon.xereon.data.category.Category
import com.xereon.xereon.data.category.source.CategoryConverter
import com.xereon.xereon.data.util.CategoryUtils
import com.xereon.xereon.databinding.FragmentCategoryBinding
import com.xereon.xereon.ui.main.MainActivity
import com.xereon.xereon.util.lists.decorations.TopBottomPaddingDecorator
import com.xereon.xereon.util.ui.doNavigate
import com.xereon.xereon.util.ui.observeLiveData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : Fragment(R.layout.fragment_category) {
    private val viewModel by viewModels<CategoryFragmentViewModel>()
    private val args by navArgs<CategoryFragmentArgs>()

    private var _binding : FragmentCategoryBinding? = null
    private val binding get() = _binding!!

    private val subCategoriesAdapter = SubCategoriesAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCategoryBinding.bind(view)

        binding.toolbar.apply {
            setNavigationOnClickListener {
                (requireActivity() as MainActivity).goBack()
            }
        }

        binding.subCategories.apply {
            itemAnimator = DefaultItemAnimator()
            addItemDecoration(TopBottomPaddingDecorator(R.dimen.spacing_ultra_tiny))
            layoutManager = LinearLayoutManager(requireContext())
            adapter = subCategoriesAdapter
            setHasFixedSize(false)
        }

        subCategoriesAdapter.setOnSubCategoryClickListener {
            doNavigate(CategoryFragmentDirections.actionCategoryFragment2ToSubCategoryFragment2(it))
        }

        viewModel.category.observeLiveData(this) {
            binding.apply {
                if (it.headerImgId != -1)
                    categoryImage.setImageResource(it.headerImgId)
                categoryName.text = it.categoryName
                categoryDescription.text = getString(it.categoryDescription)
                subCategoriesAdapter.update(it.subCategories)
                toolbar.title = it.categoryName
                categoryColor.setBackgroundColor(requireContext().getColor(it.colorId))
            }
        }

        viewModel.getCategory(args.categoryId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}