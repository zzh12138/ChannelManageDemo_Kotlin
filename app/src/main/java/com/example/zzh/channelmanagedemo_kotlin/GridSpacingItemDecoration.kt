package com.example.zzh.channelmanagedemo_kotlin

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by zhangzhihao on 2018/3/7.
 */
class GridSpacingItemDecoration(var spanCount: Int, var spacing: Int, var includeEdge: Boolean) : RecyclerView.ItemDecoration() {
    private var tabPosition = 0
    override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?) {
        var position = parent!!.getChildAdapterPosition(view)
        if (position > 0) {
            val id = parent.adapter.getItemViewType(position)
            if (id == R.layout.adapter_tab) {
                tabPosition = position
            }
            if (id == R.layout.adapter_channel) {
                if (position <= tabPosition) {
                    position--
                } else {
                    position -= (tabPosition + 1)
                }
                val column = position % spanCount
                if (includeEdge) {
                    outRect!!.left = spacing - column * spacing / spanCount
                    outRect.right = (column + 1) * spacing / spanCount
                    if(position<spanCount){
                        outRect.top=20
                    }
                    outRect.bottom=20
                }else{
                    outRect!!.left=column*spacing/spanCount
                    outRect.right=spacing-(column+1)*spacing/spanCount
                    if(position>=spanCount){
                        outRect.top=20
                    }
                }
            }
        }
    }
}