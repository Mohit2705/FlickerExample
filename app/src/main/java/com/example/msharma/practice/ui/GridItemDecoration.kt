package com.example.msharma.practice.ui

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.msharma.practice.R

class GridItemDecoration(private val context: Context) : RecyclerView.ItemDecoration() {

    private val margin: Int by lazy {
        context.resources.getDimensionPixelOffset(R.dimen.margin)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (parent.getChildLayoutPosition(view) < 3) {
            outRect.top = margin
        }
        when {
            parent.getChildLayoutPosition(view) == 0 -> outRect.left = margin
            parent.getChildLayoutPosition(view) == 2 -> outRect.right = margin
            else -> {
                outRect.left = margin / 2
                outRect.right = margin / 2
            }
        }
        outRect.bottom = margin
    }
}