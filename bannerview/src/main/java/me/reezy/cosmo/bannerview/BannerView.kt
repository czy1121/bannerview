package me.reezy.cosmo.bannerview

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import me.reezy.cosmo.bannerview.adapter.BannerAdapter
import me.reezy.cosmo.bannerview.indicator.Indicator
import kotlin.math.abs


class BannerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr), LifecycleEventObserver {


    private val banner = RecyclerView(context)
    private val pager = PagerSnapHelper()

    private val onItemTouchListener = OnItemTouchListener()

    private var activePosition: Int = 1
    private var interval: Long = 3 * 1000

    private var isRunning: Boolean = false
    private var isStarted: Boolean = false
    private var isPaused: Boolean = false
    private var isVisible: Boolean = false
    private var isDragging: Boolean = false

    private var mInterceptRequestLayout = false

    init {
        pager.attachToRecyclerView(banner)


        banner.layoutManager = BannerLayoutManager(context, LinearLayoutManager.HORIZONTAL, 75f)
        banner.overScrollMode = View.OVER_SCROLL_NEVER
        banner.isNestedScrollingEnabled = false
        banner.clipToPadding = false
        banner.itemAnimator = null
        banner.setHasFixedSize(true)
        banner.addOnItemTouchListener(onItemTouchListener)
        banner.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(view: RecyclerView, newState: Int) {
                when (newState) {
                    RecyclerView.SCROLL_STATE_DRAGGING -> reposition()
                    RecyclerView.SCROLL_STATE_IDLE -> reposition()
                    else -> {}
                }
            }
        })

        addView(banner, 0, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    var aspectRatio: Float = 0f
        set(value) {
            if (field == value) return
            field = value
            requestLayout()
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


    private val adapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {

            val itemCount = banner.adapter?.itemCount ?: 0

            activePosition = if (itemCount > 1) 1 else 0

            banner.smoothScrollToPosition(activePosition)

            updateIndicator()
            update()
        }
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                activePosition += 1
                banner.smoothScrollToPosition(activePosition)
                postDelayed(this, interval)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    val adapter: BannerAdapter<Any>? get() = banner.adapter as? BannerAdapter<Any>

    var indicator: Indicator? = null
        set(value) {
            if (field == value) return
            field?.indicatorView?.let {
                removeView(it)
            }
            field = value

            value ?: return

            val view = value.indicatorView
            if (view.parent == null) {
                val width = view.layoutParams?.width ?: LayoutParams.WRAP_CONTENT
                val height = view.layoutParams?.height ?: LayoutParams.WRAP_CONTENT

                addView(view, value.style.generateLayoutParams(width, height))
            }
            updateIndicator()
        }

    fun setParentTouchableListener(listener: (Boolean) -> Unit) {
        onItemTouchListener.setParentTouchableListener = listener
    }


    fun <Item : Any> setup(adapter: BannerAdapter<Item>): BannerView {
        if (banner.adapter != adapter) {
            banner.adapter?.unregisterAdapterDataObserver(adapterDataObserver)
            banner.adapter = adapter
            adapter.registerAdapterDataObserver(adapterDataObserver)
            updateIndicator()
        }
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

    fun resume() {
        isPaused = false
        update()
    }

    fun pause() {
        isPaused = true
        update()
    }

    private fun update() {
        val itemCount = banner.adapter?.itemCount ?: 0
        val running = itemCount > 1 && isVisible && isStarted && !isPaused && !isDragging
//        Log.e("OoO", "$running = $isVisible && $isStarted && !$isPaused && !$isDragging, $isRunning, $childCount")
        if (running != isRunning) {
            isRunning = running
            Log.e("OoO.banner", "running = $running")
            if (running) {
                postDelayed(runnable, interval)
            } else {
                removeCallbacks(runnable)
            }
        }
    }


    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> resume()
            Lifecycle.Event.ON_PAUSE -> pause()
            else -> {}
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                isDragging = true
                update()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isDragging = false
                update()
            }
        }
        return super.dispatchTouchEvent(ev)
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


    private fun updateIndicator() {
        indicator?.apply {
            val itemCount = banner.adapter?.itemCount ?: return
            val realCount = if (itemCount > 1) itemCount - 2 else 1
            val realPosition = when {
                itemCount <= 1 -> 0
                activePosition == itemCount - 1 -> 0
                activePosition == 0 -> itemCount - 2 - 1
                else -> activePosition - 1
            }
            onStateChanged(realCount, realPosition)
        }
    }

    private fun reposition() {
        val itemCount = banner.adapter?.itemCount ?: 0
        if (itemCount <= 1) return
        val lm = banner.layoutManager ?: return
        activePosition = lm.getPosition(pager.findSnapView(lm) ?: return)
        when (activePosition) {
            0 -> {
                activePosition = itemCount - 2
                scrollToPosition(activePosition)
            }

            itemCount - 1 -> {
                activePosition = 1
                scrollToPosition(activePosition)
            }

            else -> {}
        }
        updateIndicator()
    }

    private fun scrollToPosition(position: Int) {
        mInterceptRequestLayout = true
        banner.scrollToPosition(position)
        mInterceptRequestLayout = false
    }


    private class OnItemTouchListener : RecyclerView.OnItemTouchListener {

        var setParentTouchableListener: ((Boolean) -> Unit)? = null

        private var lastX = 0f
        private var lastY = 0f
        private var isMoving = false

        override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
            val setTouchable = this.setParentTouchableListener ?: return false

            // 只有一个时不请求父级禁止拦截
            if ((rv.layoutManager?.itemCount ?: 0) <= 1)  {
                return false
            }
            when (e.action) {
                MotionEvent.ACTION_DOWN -> {
                    lastX = e.x
                    lastY = e.y
                    setTouchable(false)
                }

                MotionEvent.ACTION_MOVE -> {
                    val deltaX = e.x - lastX
                    val deltaY = e.y - lastY
                    if (abs(deltaX) > abs(deltaY) * 0.2f && !isMoving) {
                        setTouchable(false)
                        isMoving = true
                    }
                    lastX = e.x
                    lastY = e.y
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (isMoving) {
                        e.action = MotionEvent.ACTION_CANCEL
                    }
                    setTouchable(true)
                    isMoving = false
                    lastX = 0f
                    lastY = 0f
                }
            }
            return false
        }

        override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {

        }

        override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

        }
    }

    private class BannerLayoutManager(context: Context, orientation: Int, var millisecondPerInch: Float) : LinearLayoutManager(context, orientation, false) {

        override fun smoothScrollToPosition(recyclerView: RecyclerView, state: RecyclerView.State, position: Int) {
            val scroller = object : LinearSmoothScroller(recyclerView.context) {
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return millisecondPerInch / displayMetrics.densityDpi
                }
            }
            scroller.targetPosition = position
            startSmoothScroll(scroller)
        }
    }
}