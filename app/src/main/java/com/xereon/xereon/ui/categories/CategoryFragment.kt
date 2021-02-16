package com.xereon.xereon.ui.categories

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.xereon.xereon.R
import com.xereon.xereon.adapter.recyclerAdapter.StoreHorizontalAdapter
import com.xereon.xereon.adapter.recyclerAdapter.SubCategoriesAdapter
import com.xereon.xereon.data.store.SimpleStore
import com.xereon.xereon.databinding.FrgCategoryBinding
import com.xereon.xereon.di.InjectPostCode
import com.xereon.xereon.ui.MainActivity
import com.xereon.xereon.util.Constants
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CategoryFragment : Fragment(R.layout.frg_category) {
    private val viewModel by viewModels<CategoryViewModel>()
    private val args by navArgs<CategoryFragmentArgs>()

    private var _binding: FrgCategoryBinding? = null
    private val binding get() = _binding!!

    private val storesAdapter = StoreHorizontalAdapter()
    private var subCategoriesAdapter = SubCategoriesAdapter()

    @JvmField @Inject @InjectPostCode var postcode: String = Constants.DEFAULT_POSTCODE

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FrgCategoryBinding.bind(view)
        (requireActivity() as MainActivity).setActionBarTitle(args.category.categoryName)

        viewModel.getExampleStores(args.category.categoryIndex, postcode)

        subCategoriesAdapter.submitList(args.category.subCategories)
        subCategoriesAdapter.setOnItemClickListener(object : SubCategoriesAdapter.ItemClickListener{
            override fun onItemClick(subCategory: String) {
                val action = CategoryFragmentDirections.actionCategoryToSubCategory(type = subCategory)
                findNavController().navigate(action)
            }
        })

        storesAdapter.setOnItemClickListener(object: StoreHorizontalAdapter.ItemClickListener{
            override fun onItemClick(simpleStore: SimpleStore) {
                val action = CategoryFragmentDirections.actionToStore(simpleStore = simpleStore)
                findNavController().navigate(action)
            }
        })

        binding.apply {
            category = args.category

            categorySubcategoriesList.apply {
                itemAnimator = null
                adapter = subCategoriesAdapter
            }

            categoryCloseList.apply {
                setHasFixedSize(true)
                adapter = storesAdapter
            }
        }

        subscribeToObserver()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun subscribeToObserver() {
        viewModel.exampleStores.observe(viewLifecycleOwner, Observer {event ->
            when (event) {
                is CategoryViewModel.CategoryEvent.Success -> {
                    storesAdapter.submitList(event.examplesData)
                }
                is CategoryViewModel.CategoryEvent.Failure -> {
                    displayError(R.string.no_connection_exception)
                }
                is CategoryViewModel.CategoryEvent.Loading -> {

                }
                else -> Unit
            }
        })
    }

    private fun displayError(@StringRes messageId: Int) {
        val snackBar = Snackbar.make(requireView(), messageId, Snackbar.LENGTH_INDEFINITE)
        snackBar.setAction("Retry") {
            viewModel.getExampleStores(args.category.categoryIndex, "")
        }
        snackBar.setActionTextColor(Color.WHITE)
        val snackBarView: View = snackBar.view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            snackBarView.setBackgroundColor(resources.getColor(R.color.error, null))
        snackBar.show()
    }
}