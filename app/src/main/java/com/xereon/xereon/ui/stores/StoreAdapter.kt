package com.xereon.xereon.ui.stores

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.xereon.xereon.ui.stores.items.StoreBasicInformation
import com.xereon.xereon.ui.stores.items.StoreItem
import com.xereon.xereon.ui.stores.items.StoreOpeningHour
import com.xereon.xereon.ui.stores.items.StorePeakTimes
import com.xereon.xereon.util.lists.BindableVH
import com.xereon.xereon.util.lists.diffutil.AsyncDiffUtilAdapter
import com.xereon.xereon.util.lists.diffutil.AsyncDiffer
import com.xereon.xereon.util.lists.modular.ModularAdapter
import com.xereon.xereon.util.lists.modular.mods.DataBinderMod
import com.xereon.xereon.util.lists.modular.mods.StableIdMod
import com.xereon.xereon.util.lists.modular.mods.TypedVHCreatorMod

class StoreAdapter : ModularAdapter<StoreAdapter.StoreItemVH<StoreItem, ViewBinding>>(),
    AsyncDiffUtilAdapter<StoreItem> {

    override val asyncDiffer: AsyncDiffer<StoreItem> = AsyncDiffer(adapter = this)

    init {
        modules.addAll(listOf(
            StableIdMod(data),
            DataBinderMod<StoreItem, StoreAdapter.StoreItemVH<StoreItem, ViewBinding>>(data),
            TypedVHCreatorMod({ data[it] is StoreBasicInformation.Item }) { StoreBasicInformation(it) },
            TypedVHCreatorMod({ data[it] is StoreOpeningHour.Item }) { StoreOpeningHour(it) },
            TypedVHCreatorMod({ data[it] is StorePeakTimes.Item }) { StorePeakTimes(it) },
        ))
    }

    override fun getItemCount(): Int = data.size

    abstract class StoreItemVH<Item: StoreItem, VB: ViewBinding>(
        @LayoutRes layoutRes: Int,
        parent: ViewGroup
    ) : ModularAdapter.VH(layoutRes, parent), BindableVH<Item, VB>
}