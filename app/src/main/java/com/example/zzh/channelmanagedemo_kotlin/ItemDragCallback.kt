package com.example.zzh.channelmanagedemo_kotlin

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG
import android.view.View

/**
 * Created by zhangzhihao on 2018/3/7.
 */
class ItemDragCallback(var mAdapter: ChannelAdapter, var mPadding: Int) : ItemTouchHelper.Callback() {
    private val mPaint: Paint = Paint()

    init {
        mPaint.color = Color.GRAY
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 1f
        mPaint.style = Paint.Style.STROKE
        val pathEffect = DashPathEffect(FloatArray(2, { 5f }), 5f)
        mPaint.pathEffect = pathEffect
    }

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        //固定位置及tab下面的channel不能拖动
        val position = viewHolder!!.layoutPosition
        if (position < mAdapter.fixSize + 1 || position > mAdapter.selectedSize) {
            return makeMovementFlags(0, 0)
        }
        val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        return makeMovementFlags(dragFlags, 0)
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        val fromPosition = viewHolder!!.layoutPosition    //拖动的位置
        val toPosition = target!!.layoutPosition         //释放的位置
        //固定位置及tab下面的channel不能移动
        if (toPosition < mAdapter.fixSize + 1 || toPosition > mAdapter.selectedSize) {
            return false
        }
        mAdapter.itemMove(fromPosition, toPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
        //滑动重写这里
    }

    override fun onChildDrawOver(c: Canvas?, recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        if (dX != 0f && dY != 0f || isCurrentlyActive) {
            //长按拖拽时底部绘制一个虚线矩形
            c!!.drawRect(viewHolder!!.itemView.left.toFloat(), (viewHolder.itemView.top - mPadding).toFloat(), viewHolder.itemView.right.toFloat(), viewHolder.itemView.bottom.toFloat(), mPaint)
        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)
        if (actionState == ACTION_STATE_DRAG) {
            //长按时调用 设置颜色 阴影
            val holder = viewHolder as ChannelAdapter.ChannelHolder
            holder.name.setBackgroundColor(Color.parseColor("#FDFDFE"))
            holder.delete.visibility= View.GONE
            holder.name.elevation=5f
        }
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        super.clearView(recyclerView, viewHolder)
        //重置view
        val holder=viewHolder as ChannelAdapter.ChannelHolder
        holder.name.setBackgroundColor(Color.parseColor("#f0f0f0"))
        holder.delete.visibility=View.VISIBLE
        holder.name.elevation=0f
    }

}