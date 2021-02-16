package com.xereon.xereon.ui.stores.items

import android.text.Html
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.xereon.xereon.R
import com.xereon.xereon.data.category.source.CategoryConverter
import com.xereon.xereon.data.store.Store
import com.xereon.xereon.databinding.StoreBasicInformationBinding
import com.xereon.xereon.ui.stores.StoreAdapter

class StoreBasicInformation(parent: ViewGroup) :
    StoreAdapter.StoreItemVH<StoreBasicInformation.Item, StoreBasicInformationBinding>(
        R.layout.store_basic_information, parent
    ) {

    override val viewBinding = lazy {
        StoreBasicInformationBinding.bind(itemView)
    }

    override val onBindData: StoreBasicInformationBinding.(
        item: Item,
        payloads: List<Any>
    ) -> Unit = { item, _ ->
        with(item.data) {
            storeName.text = Html.fromHtml(name)
            Glide.with(context).load(logoImageURL).into(storeLogoImage)
            Glide.with(context).load(officeImageURL).into(storeOfficeImage)
            storeType.text = type
            storeType.setBackgroundColor(context.getColor(CategoryConverter.getCategoryColor(category)))
            storeAddress.text = completeAddress
            storePhoneNumber.text = phone
            storeWebsite.text = website
            storeDescription.text = description

            storeStartChat.setOnClickListener { item.onEnterChatClicked.invoke() }
            storeSaveAsFavorite.setOnClickListener { item.onSaveAsFavoriteClicked.invoke() }
            storeNavigate.setOnClickListener { item.onNavigateClicked.invoke() }
        }
    }

    data class Item(
        val data: Store,
        val onNavigateClicked: () -> Unit,
        val onSaveAsFavoriteClicked: () -> Unit,
        val onEnterChatClicked: () -> Unit
    ) : StoreItem {
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