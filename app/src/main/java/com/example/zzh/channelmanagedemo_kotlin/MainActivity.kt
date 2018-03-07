package com.example.zzh.channelmanagedemo_kotlin

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ChannelAdapter.OnItemRangeChangeListener {


    private val select = arrayOf("要闻", "体育", "新时代", "汽车", "时尚", "国际", "电影", "财经", "游戏", "科技", "房产", "政务", "图片", "独家")
    private val recommend = arrayOf("娱乐", "军事", "文化", "视频", "股票", "动漫", "理财", "电竞", "数码", "星座", "教育", "美容", "旅游")
    private val city = arrayOf("重庆", "深圳", "汕头", "东莞", "佛山", "江门", "湛江", "惠州", "中山", "揭阳", "韶关", "茂名", "肇庆", "梅州", "汕尾", "河源", "云浮", "四川")
    val mList = arrayListOf<ChannelBean>()
    var mAdapter: ChannelAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val manager = GridLayoutManager(this, 4)
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return mList[position].spanSize
            }
        }
        recyclerView.layoutManager = manager
        val animator = DefaultItemAnimator()
        animator.moveDuration = 300      //设置动画时间
        animator.removeDuration = 0
        recyclerView.itemAnimator = animator
        createData()
        recyclerView.adapter = mAdapter
        val m = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val spacing = ((m.defaultDisplay.width - this.dip2px(70f) * 4) / 5).toInt()
        recyclerView.addItemDecoration(GridSpacingItemDecoration(4, spacing, true))
        val callback = ItemDragCallback(mAdapter!!, 2)
        val helper = ItemTouchHelper(callback)
        helper.attachToRecyclerView(recyclerView)
    }

    private fun createData() {
        mList.add(ChannelBean("", 4, R.layout.adapter_title, false))
        select.mapTo(mList) { ChannelBean(it, 1, R.layout.adapter_channel, true) }
        mList.add(ChannelBean("", 4, R.layout.adapter_tab, false))
        val recommendList = arrayListOf<ChannelBean>()
        recommend.mapTo(recommendList) { ChannelBean(it, 1, R.layout.adapter_channel, true) }
        val cityList = arrayListOf<ChannelBean>()
        city.mapTo(cityList) { ChannelBean(it, 1, R.layout.adapter_channel, false) }
        cityList.add(ChannelBean("", 4, R.layout.adapter_more_channel, false))
        mList.addAll(recommendList)
        mAdapter = ChannelAdapter(this, mList, recommendList, cityList)
        mAdapter!!.fixSize = 1
        mAdapter!!.selectedSize = select.size
        mAdapter!!.onItemRangeChangeListener = this
    }

    override fun refreshItemDecoration() {
        recyclerView.invalidateItemDecorations()
    }
}
