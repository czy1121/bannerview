package me.reezy.cosmo.bannerview


import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver


class BannerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs), LifecycleEventObserver {

    fun interface OnDataChangedListener {
        fun onDataChanged(itemCount: Int)
    }

    fun interface OnSelectListener {
        fun onSelected(position: Int)
    }


    fun interface OnScrollListener {
        fun onScrolled(position: Int, positionOffset: Float)
    }

    private val banner = RecyclerView(context)
    private val pager = PagerSnapHelper()
    private val layoutManager = BannerLayoutManager(context, LinearLayoutManager.HORIZONTAL)

    private var isRunning: Boolean = false
    private var isStarted: Boolean = false
    private var isPaused: Boolean = false
    private var isVisible: Boolean = false
    private var isDragging: Boolean = false

    private var mInterval: Long = 3 * 1000
    private var mSelectedPosition: Int = 1
    private var mInterceptRequestLayout = false

    private var mAdapter: Adapter<Any>? = null

    init {
        pager.attachToRecyclerView(banner)


        banner.layoutManager = layoutManager
        banner.overScrollMode = View.OVER_SCROLL_NEVER
        banner.isNestedScrollingEnabled = false
        banner.clipToPadding = false
        banner.itemAnimator = null
        banner.setHasFixedSize(true)

        banner.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(view: RecyclerView, newState: Int) {
                when (newState) {
//                    RecyclerView.SCROLL_STATE_DRAGGING -> reposition()
                    RecyclerView.SCROLL_STATE_IDLE -> reposition()
                    else -> {}
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val itemCount = bannerSize
                if (itemCount == 0) return

                val position = layoutManager.findFirstVisibleItemPosition()
                if (position == RecyclerView.NO_POSITION) {
                    return
                }
                val view = layoutManager.findViewByPosition(position) ?: return

                val offsetPx = -view.left
                if (offsetPx < 0) {
                    return
                }

                val sizePx = view.width
                val offset = if (sizePx == 0) 0f else offsetPx.toFloat() / sizePx

                when {
                    offset == 0f -> mOnScrollListener?.onScrolled(position - 1, offset)
                    position == itemCount - 2 -> mOnScrollListener?.onScrolled(0, 1 - offset)
                    position == 0 -> mOnScrollListener?.onScrolled(itemCount - 4, 1 - offset)
                    else -> mOnScrollListener?.onScrolled(position - 1, offset)
                }
            }

        })

        addView(banner, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private val bannerSize: Int get() = mAdapter?.itemCount ?: 0

    val itemCount: Int get() = mAdapter?.originList?.size ?: 0

//    val adapter: Adapter<Any>? get() = mAdapter

    var aspectRatio: Float = 0f
        set(value) {
            if (field == value) return
            field = value
            requestLayout()
        }



    private val adapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            mOnDataChangedListener?.onDataChanged(itemCount)

            mSelectedPosition = if (bannerSize > 1) 1 else 0
            scrollToPosition(mSelectedPosition)

            onSelect()
            update()
        }

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = onChanged()
        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = onChanged()
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                mSelectedPosition += 1
                layoutManager.useBannerScroller = true
                banner.smoothScrollToPosition(mSelectedPosition)
                onSelect()
                postDelayed(this, mInterval)
            }
        }
    }



    private var mOnDataChangedListener: OnDataChangedListener? = null
    private var mOnSelectListener: OnSelectListener? = null
    private var mOnScrollListener: OnScrollListener? = null
    private var setAncestorTouchable: (Boolean) -> Unit = {
        requestDisallowInterceptTouchEvent(!it)
    }

    fun setOnDataChangedListener(listener: OnDataChangedListener) {
        mOnDataChangedListener = listener
        mOnDataChangedListener?.onDataChanged(itemCount)
    }

    fun setOnSelectListener(listener: OnSelectListener) {
        mOnSelectListener = listener
        onSelect()
    }

    fun setOnScrollListener(listener: OnScrollListener) {
        mOnScrollListener = listener
    }

    fun setAncestorTouchableListener(listener: (Boolean) -> Unit) {
        setAncestorTouchable = listener
    }

    fun setInterval(value: Long): BannerView {
        mInterval = value
        return this
    }

    fun setCurrentItem(value: Int) {
        val count = itemCount
        if (count <= 0) return
        mSelectedPosition = if (value >= count) count else (value + 1)

        layoutManager.useBannerScroller = false
        banner.smoothScrollToPosition(mSelectedPosition)
    }


    fun <T : Any> setAdapter(adapter: Adapter<T>): BannerView {
        if (banner.adapter != adapter) {
            banner.adapter?.unregisterAdapterDataObserver(adapterDataObserver)
            banner.adapter = adapter
            @Suppress("UNCHECKED_CAST")
            mAdapter = adapter as Adapter<Any>
            adapter.registerAdapterDataObserver(adapterDataObserver)
            onSelect()
        }
        return this
    }

    fun <T : Any> setData(data: List<T>): BannerView {
        mAdapter?.setData(data)
        return this
    }


    fun bind(owner: LifecycleOwner): BannerView {
        owner.lifecycle.addObserver(this)
        return this
    }


    fun start() {
        isStarted = true
        update()
    }


    private fun update() {
        val running = bannerSize > 1 && isVisible && isStarted && !isPaused && !isDragging
        if (running != isRunning) {
            isRunning = running
//            Log.e("OoO.banner", "running = $running")
            if (running) {
                postDelayed(runnable, mInterval)
            } else {
                removeCallbacks(runnable)
            }
        }
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (aspectRatio > 0f) {
            val wSize = MeasureSpec.getSize(widthMeasureSpec)
            val wMode = MeasureSpec.getMode(widthMeasureSpec)
            if (wMode == MeasureSpec.EXACTLY) {
                val h = (wSize.toFloat() / aspectRatio).toInt()
                super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY))
                return
            }

            val hSize = MeasureSpec.getSize(heightMeasureSpec)
            val hMode = MeasureSpec.getMode(heightMeasureSpec)
            if (hMode == MeasureSpec.EXACTLY) {
                val w = (hSize.toFloat() * aspectRatio).toInt()
                super.onMeasure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), heightMeasureSpec)
                return
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun requestLayout() {
        if (mInterceptRequestLayout) {
            super.requestLayout()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isVisible = true
        update()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isVisible = false
        update()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                isPaused = false
                update()
            }

            Lifecycle.Event.ON_PAUSE -> {
                isPaused = true
                update()
            }

            else -> {}
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                if (bannerSize > 1) {
                    setAncestorTouchable(false)
                }
                isDragging = true
                update()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (bannerSize > 1) {
                    setAncestorTouchable(true)
                }
                isDragging = false
                update()
            }
        }
        return super.dispatchTouchEvent(ev)
    }


    private fun scrollToPosition(position: Int) {
        mInterceptRequestLayout = true
        banner.scrollToPosition(position)
        mInterceptRequestLayout = false
    }

    private fun onSelect() {
        mOnSelectListener?.onSelected(mAdapter?.getOriginPosition(mSelectedPosition) ?: return)
        invalidate()
    }


    private fun reposition() {
        if (bannerSize == 0) return
        mSelectedPosition = layoutManager.getPosition(pager.findSnapView(layoutManager) ?: return)
        when (mSelectedPosition) {
            0 -> {
                mSelectedPosition = bannerSize - 2
                scrollToPosition(mSelectedPosition)
            }

            bannerSize - 1 -> {
                mSelectedPosition = 1
                scrollToPosition(mSelectedPosition)
            }

            else -> {}
        }
        onSelect()
    }


    private class BannerLayoutManager(context: Context, orientation: Int) : LinearLayoutManager(context, orientation, false) {

        var useBannerScroller: Boolean = false

        override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
            val scroller = if (useBannerScroller) BannerScroller(recyclerView.context) else LinearSmoothScroller(recyclerView.context)
            scroller.targetPosition = position
            startSmoothScroll(scroller)
        }
    }

    private class BannerScroller(context: Context, val millisecondPerInch: Float = 75f) : LinearSmoothScroller(context) {
        override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
            return millisecondPerInch / displayMetrics.densityDpi
        }
    }


    private class DefaultDiffCallback<Item: Any> : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }

    abstract class Adapter<Item : Any>(callback: DiffUtil.ItemCallback<Item> = DefaultDiffCallback()) : ListAdapter<Item, ViewHolder>(callback) {

        internal var originList: List<Item> = listOf()

        fun setData(data: List<Item>) {
            if (originList == data) return
            originList = data
            submitList(if (data.size > 1) (listOf(data[data.size - 1]) + data + data[0]) else data)
        }


        fun getOriginPosition(position: Int) = when {
            currentList.size <= 1 -> 0
            position == currentList.size - 1 -> 0
            position == 0 -> currentList.size - 2 - 1
            else -> position - 1
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
}


