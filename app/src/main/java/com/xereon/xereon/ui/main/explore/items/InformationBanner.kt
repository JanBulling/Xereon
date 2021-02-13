package com.xereon.xereon.ui.main.explore.items

import android.view.ViewGroup
import com.xereon.xereon.R
import com.xereon.xereon.data.explore.ExploreBannerData
import com.xereon.xereon.databinding.ExploreInformationBannerBinding
import com.xereon.xereon.ui.main.explore.ExploreAdapter

class InformationBanner(parent: ViewGroup) :
    ExploreAdapter.ExploreItemVH<InformationBanner.Item, ExploreInformationBannerBinding>(
        R.layout.explore_information_banner, parent
    ) {

    override val viewBinding = lazy {
        ExploreInformationBannerBinding.bind(itemView)
    }

    override val onBindData: ExploreInformationBannerBinding.(
        item: Item,
        payloads: List<Any>
    ) -> Unit = { item, _ ->
        with(item.data){
            bannerText.text = title
            bannerText.setTextColor(fontColor)
            bannerText.setBackgroundColor(backgroundColor)
        }
    }

    data class Item(
        val data: ExploreBannerData
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