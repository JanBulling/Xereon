package com.xereon.xereon.util.lists.decorations

import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

class TopBottomPaddingDecorator(
    @DimenRes val topPadding: Int,
    @DimenRes val bottomPadding: Int = topPadding
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildAdapterPosition(view)

        if (itemPosition == RecyclerView.NO_POSITION) return

        val resources = parent.context.resources

        if (itemPosition == 0)
            outRect.bottom = resources.getDimensionPixelSize(bottomPadding)

        else {
            parent.adapter?.let {
                if (itemPosition != it.itemCount - 1)
                    outRect.bottom = resources.getDimensionPixelSize(bottomPadding)

                outRect.top = resources.getDimensionPixelSize(topPadding)
            }
        }
    }

}