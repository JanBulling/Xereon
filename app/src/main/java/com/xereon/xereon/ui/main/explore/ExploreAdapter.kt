package com.xereon.xereon.ui.main.explore

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.xereon.xereon.ui.main.explore.items.*
import com.xereon.xereon.util.lists.BindableVH
import com.xereon.xereon.util.lists.diffutil.AsyncDiffUtilAdapter
import com.xereon.xereon.util.lists.diffutil.AsyncDiffer
import com.xereon.xereon.util.lists.modular.ModularAdapter
import com.xereon.xereon.util.lists.modular.mods.DataBinderMod
import com.xereon.xereon.util.lists.modular.mods.StableIdMod
import com.xereon.xereon.util.lists.modular.mods.TypedVHCreatorMod

class ExploreAdapter : ModularAdapter<ExploreAdapter.ExploreItemVH<ExploreItem, ViewBinding>>(),
    AsyncDiffUtilAdapter<ExploreItem> {

    override val asyncDiffer: AsyncDiffer<ExploreItem> = AsyncDiffer(adapter = this)

    init {
        modules.addAll(listOf(
            StableIdMod(data),
            DataBinderMod<ExploreItem, ExploreItemVH<ExploreItem, ViewBinding>>(data),
            TypedVHCreatorMod({ data[it] is InformationBanner.Item }) { InformationBanner(it) },
            TypedVHCreatorMod({ data[it] is PopularCategories.Item }) { PopularCategories(it) },
            TypedVHCreatorMod({ data[it] is HorizontalProducts.Item }) { HorizontalProducts(it) },
            TypedVHCreatorMod({ data[it] is HorizontalStores.Item }) { HorizontalStores(it) },
        ))
    }

    override fun getItemCount(): Int = data.size

    abstract class ExploreItemVH<Item : ExploreItem, VB : ViewBinding>(
        @LayoutRes layoutRes: Int,
        parent: ViewGroup
    ) : ModularAdapter.VH(layoutRes, parent), BindableVH<Item, VB>
}