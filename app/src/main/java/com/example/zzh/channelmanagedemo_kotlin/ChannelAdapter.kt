package com.example.zzh.channelmanagedemo_kotlin

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

/**
 * Created by zhangzhihao on 2018/3/6.
 */
class ChannelAdapter(var mContext: Context, var mList: ArrayList<ChannelBean>, var recommendList: ArrayList<ChannelBean>, var cityList: ArrayList<ChannelBean>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var selectedSize: Int = 0             //已选
    var fixSize: Int = 0                 //固定频道数目
    var isRecommend: Boolean = true      //当前是否显示推荐频道
    var mLeft: Int = -1                  //推荐频道蓝色线条距离屏幕左边的距离
    var mRight: Int = -1                 //城市频道蓝色线条距离屏幕左边的距离
    var mTabY: Int = 0                   //tab距离parent的Y的距离
    var onItemRangeChangeListener: OnItemRangeChangeListener? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(mContext).inflate(viewType, parent, false)
        return when (viewType) {
            R.layout.adapter_channel -> ChannelHolder(view)
            R.layout.adapter_more_channel -> MoreChannelHolder(view)
            R.layout.adapter_tab -> TabHolder(view)
            else -> TitleHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        if (holder is ChannelHolder) {
            setChannel(holder, position)
        } else if (holder is TabHolder) {
            setTab(holder)
        }

    }

    override fun getItemCount(): Int {
        return mList.size
    }

    override fun getItemViewType(position: Int): Int {
        return mList[position].layoutId
    }

    private fun setChannel(holder: ChannelHolder, position: Int) {
        holder.name.text = mList[position].name
        holder.name.setOnClickListener {
            if (holder.layoutPosition < selectedSize + 1) {
                //tab上面的 点击移除
                if (holder.layoutPosition > fixSize) {
                    removeFromSelected(holder)
                }
            } else {
                //tab下面的 点击添加到已选频道
                selectedSize++
                itemMove(holder.layoutPosition, selectedSize)
                notifyItemChanged(selectedSize)
                //刷新itemDecoration
                onItemRangeChangeListener?.let { onItemRangeChangeListener!!.refreshItemDecoration() }
            }
        }
        holder.name.setOnLongClickListener { true }
        holder.delete.setOnClickListener { removeFromSelected(holder) }
        //tab下面及固定频道不显示删除按钮
        if (position - 1 < fixSize || position > selectedSize) {
            holder.delete.visibility = View.GONE
        } else {
            holder.delete.visibility = View.VISIBLE
        }
    }

    private fun setTab(holder: TabHolder) {
        val params = holder.indicator.layoutParams as LinearLayout.LayoutParams
        //计算蓝色线条位置
        if (mLeft == -1) {
            holder.recommend.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    val layout = holder.recommend.layout
                    //textView左边距离+第一个文字绘制位置-padding
                    mLeft = (holder.recommend.left + layout.getPrimaryHorizontal(0) - mContext.dip2px(10f)).toInt()
                    params.leftMargin = mLeft;
                    holder.indicator.layoutParams = params
                    holder.recommend.viewTreeObserver.removeOnPreDrawListener(this)
                    return true
                }
            })
        }

        holder.city.setOnClickListener {
            if (isRecommend) {
                holder.city.typeface = Typeface.DEFAULT_BOLD
                holder.recommend.typeface = Typeface.DEFAULT
                if (mRight == -1) {
                    mRight = (mLeft + holder.city.left - mContext.dip2px(10f)).toInt()
                }
                params.leftMargin = mRight
                isRecommend = false
                recommendList.clear()
                recommendList.addAll(mList.subList(selectedSize + 2, mList.size))
                mList.removeAll(recommendList)
                mList.addAll(cityList)
                notifyDataSetChanged()
            }
        }

        holder.recommend.setOnClickListener {
            if (!isRecommend) {
                holder.city.typeface = Typeface.DEFAULT
                holder.recommend.typeface = Typeface.DEFAULT_BOLD
                params.leftMargin = mLeft
                isRecommend = true
                cityList.clear()
                cityList.addAll(mList.subList(selectedSize + 2, mList.size))
                mList.removeAll(cityList)
                mList.addAll(recommendList)
                notifyDataSetChanged()
            }
        }
        holder.itemView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                mTabY = holder.itemView.top
                return true
            }

        })

    }

    private fun removeFromSelected(holder: ChannelHolder) {
        holder.delete.visibility = View.GONE
        val position = holder.layoutPosition
        val bean = mList[position]
        if ((isRecommend && bean.isRecommend) || (!isRecommend && !bean.isRecommend)) {
            //移除的频道属于当前tab显示的频道，直接调用系统方法移除
            itemMove(position, selectedSize + 1)
            notifyItemRangeChanged(selectedSize + 1, 1)
            //刷新itemDecoration
            onItemRangeChangeListener?.let { onItemRangeChangeListener!!.refreshItemDecoration() }
        } else {
            //不属于当前tab显示的频道
            removeAnimation(holder.itemView, (if (isRecommend) mRight else mLeft).toFloat(), mTabY.toFloat(), position)
        }
        selectedSize--
    }

    private fun removeAnimation(view: View, x: Float, y: Float, position: Int) {
        val fromX = view.left
        val fromY = view.top
        val animatorX = ObjectAnimator.ofFloat(view, "translationX", 0f, x - fromX)
        val animatorY = ObjectAnimator.ofFloat(view, "translationY", 0f, y - fromY)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        val set = AnimatorSet()
        set.playTogether(animatorX, animatorY, alpha)
        set.duration = 350
        set.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
                if (isRecommend) {
                    cityList.add(0, mList[position])
                } else {
                    recommendList.add(0, mList[position])
                }
                mList.removeAt(position)
                notifyItemRemoved(position)
                onItemRangeChangeListener?.let { onItemRangeChangeListener!!.refreshItemDecoration() }
                //这里需要重置view的属性
                resetView(view, x - fromX, y - fromY)
            }
        })
        set.start()
    }

    private fun resetView(view: View, toX: Float, toY: Float) {
        val animatorX = ObjectAnimator.ofFloat(view, "translationX", -toX, 0f)
        val animatorY = ObjectAnimator.ofFloat(view, "translationY", -toY, 0f)
        val alpha = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f)
        val set = AnimatorSet()
        set.playTogether(animatorX, animatorY, alpha)
        set.duration = 0
        set.startDelay = 5
        set.start()
    }

    fun itemMove(fromPosition: Int, toPosition: Int) {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(mList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(mList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    class ChannelHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.channel_name)
        val delete: ImageView = itemView.findViewById(R.id.channel_delete)
    }

    class TabHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recommend: TextView = itemView.findViewById(R.id.recommend_channel)
        val city: TextView = itemView.findViewById(R.id.city_channel)
        val indicator: View = itemView.findViewById(R.id.indicator)
    }

    class MoreChannelHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    class TitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface OnItemRangeChangeListener {
        fun refreshItemDecoration()
    }

}
