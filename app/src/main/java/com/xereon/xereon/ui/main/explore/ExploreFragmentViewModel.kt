package com.xereon.xereon.ui.main.explore

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.navigation.NavDirections
import com.xereon.xereon.R
import com.xereon.xereon.data.explore.ExploreBannerData
import com.xereon.xereon.data.category.source.CategoryProvider
import com.xereon.xereon.data.explore.source.ExploreDataProvider
import com.xereon.xereon.storage.LocalData
import com.xereon.xereon.ui.main.explore.items.*
import com.xereon.xereon.util.Resource
import com.xereon.xereon.util.ui.SingleLiveEvent
import com.xereon.xereon.util.viewmodel.XereonViewModel
import kotlinx.coroutines.flow.map

class ExploreFragmentViewModel @ViewModelInject constructor(
    exploreDataProvider: ExploreDataProvider,
    private val categoryProvider: CategoryProvider,
    private val localData: LocalData,
) : XereonViewModel() {

    val routeToScreen = SingleLiveEvent<NavDirections>()
    val hasNewMessages = SingleLiveEvent<Boolean>()
    val exceptions = SingleLiveEvent<Int>()

    val exploreItems: LiveData<List<ExploreItem>> = exploreDataProvider.current.map {exploreData ->
        if (exploreData is Resource.Success)
            hasNewMessages.postValue(exploreData.data!!.chatNewMessages)
        else
            exceptions.postValue(exploreData.message!!)

        mutableListOf<ExploreItem>().apply {
            /* Banner on top. If no connection is available, a banner with a default text is showing */
            if (exploreData is Resource.Success)
                add(InformationBanner.Item(data = exploreData.data!!.bannerData))
            else
                add(InformationBanner.Item(data = ExploreBannerData("Wilkommen bei XEREON!")))

            /* Popular categories item */
            add(PopularCategories.Item(data = categoryProvider.getPopularCategories{
                    routeToScreen.postValue(ExploreFragmentDirections.actionExploreFragmentToCategoryFragment2(it.index))
                },
                seeMoreCategoriesAction = {
                    routeToScreen.postValue(ExploreFragmentDirections.actionExploreFragmentToAllCategoriesFragment())
                }))

            if (exploreData is Resource.Success) {
                /* Horizontal row of recommendation products */
                add(HorizontalProducts.Item(data = exploreData.data!!.recommendations,
                    onProductClickAction = {
                        routeToScreen.postValue(
                            ExploreFragmentDirections.actionExploreFragmentToProductFragment(
                                it.id,
                                it.name
                            )
                        )
                    },
                    title = R.string.recommendations, subtitle = R.string.recommendations_description))

                /* Horizontal row of new stores in the area */
                add(HorizontalStores.Item(data = exploreData.data.newStores,
                    onStoreClickAction = {
                        routeToScreen.postValue(
                            ExploreFragmentDirections.actionExploreFragmentToStoreFragment(
                                it.id,
                                it.name
                            )
                        )
                    },
                    title = R.string.new_stores,
                    city = localData.getCity(),
                    subtitle = R.string.new_stores_description))

                /* Horizontal row of popular products */
                add(HorizontalProducts.Item(data = exploreData.data.popular,
                    onProductClickAction = {
                        routeToScreen.postValue(
                            ExploreFragmentDirections.actionExploreFragmentToProductFragment(
                                it.id,
                                it.name
                            )
                        )
                    },
                    title = R.string.popular, subtitle = R.string.popular_description))
            }
        }
    }.asLiveData()

    companion object {
        private const val TAG = "ExploreFragmentVM"
    }
}