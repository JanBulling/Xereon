package com.xereon.xereon.ui.main.category

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.xereon.xereon.data.category.Category
import com.xereon.xereon.data.category.source.CategoryProvider
import com.xereon.xereon.util.viewmodel.XereonViewModel

class CategoryFragmentViewModel @ViewModelInject constructor(
    private val categoryProvider: CategoryProvider,
) : XereonViewModel() {

    val category: MutableLiveData<Category> = MutableLiveData()

    fun getCategory(categoryIndex: Int) {
        if (category.value == null && categoryIndex != -1)
            category.value = categoryProvider.getCategory(categoryIndex)
    }

}