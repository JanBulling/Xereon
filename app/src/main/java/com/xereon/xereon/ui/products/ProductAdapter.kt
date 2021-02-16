package com.xereon.xereon.ui.products

import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.viewbinding.ViewBinding
import com.xereon.xereon.ui.products.items.ProductAddToCart
import com.xereon.xereon.ui.products.items.ProductBasicInformation
import com.xereon.xereon.ui.products.items.ProductItem
import com.xereon.xereon.util.lists.BindableVH
import com.xereon.xereon.util.lists.diffutil.AsyncDiffUtilAdapter
import com.xereon.xereon.util.lists.diffutil.AsyncDiffer
import com.xereon.xereon.util.lists.modular.ModularAdapter
import com.xereon.xereon.util.lists.modular.mods.DataBinderMod
import com.xereon.xereon.util.lists.modular.mods.StableIdMod
import com.xereon.xereon.util.lists.modular.mods.TypedVHCreatorMod

class ProductAdapter : ModularAdapter<ProductAdapter.ProductItemVH<ProductItem, ViewBinding>>(),
    AsyncDiffUtilAdapter<ProductItem> {

    override val asyncDiffer: AsyncDiffer<ProductItem> = AsyncDiffer(adapter = this)

    init {
        modules.addAll(listOf(
            StableIdMod(data),
            DataBinderMod<ProductItem, ProductItemVH<ProductItem, ViewBinding>>(data),
            TypedVHCreatorMod({ data[it] is ProductBasicInformation.Item }) { ProductBasicInformation(it) },
            TypedVHCreatorMod({ data[it] is ProductAddToCart.Item }) { ProductAddToCart(it) },
        ))
    }

    override fun getItemCount(): Int = data.size

    abstract class ProductItemVH<Item: ProductItem, VB: ViewBinding>(
        @LayoutRes layoutRes: Int,
        parent: ViewGroup
    ) : ModularAdapter.VH(layoutRes, parent), BindableVH<Item, VB>
}