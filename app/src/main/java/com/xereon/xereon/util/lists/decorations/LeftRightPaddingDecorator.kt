package com.xereon.xereon.util.lists.decorations

import android.graphics.Rect
import android.view.View
import androidx.annotation.DimenRes
import androidx.recyclerview.widget.RecyclerView

class LeftRightPaddingDecorator(
    @DimenRes val startPadding: Int,
    @DimenRes val endPadding: Int = startPadding
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        val itemPosition = parent.getChildAdapterPosition(view)

        if (itemPosition == RecyclerView.NO_POSITION) return

        val resources = parent.context.resources

        outRect.left = resources.getDimensionPixelSize(startPadding)
        outRect.right = resources.getDimensionPixelSize(endPadding)
    }
}
