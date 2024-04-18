package me.reezy.cosmo.bannerview

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
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
import me.reezy.cosmo.bannerview.indicator.BaseIndicator


class BannerView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr), LifecycleEventObserver {


    fun interface OnActiveChangedListener {
        fun onActiveChanged(position: Int, count: Int)
    }

    private val banner = RecyclerView(context)
    private val pager = PagerSnapHelper()

    private var active: Int = 1
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


    private val adapterDataObserver = object : AdapterDataObserver() {
        override fun onChanged() {
            active = if (itemCount > 1) 1 else 0

            scrollToPosition(active)

            onActiveChanged()
            update()
        }
    }

    private val runnable: Runnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                active += 1
                banner.smoothScrollToPosition(active)
                onActiveChanged()
                postDelayed(this, interval)
            }
        }
    }

    val itemCount: Int get() = banner.adapter?.itemCount ?: 0

    @Suppress("UNCHECKED_CAST")
    val adapter: BannerAdapter<Any>? get() = banner.adapter as? BannerAdapter<Any>


    private var activeChangedListener: OnActiveChangedListener? = null

    fun setOnActiveChangedListener(listener: OnActiveChangedListener) {
        activeChangedListener = listener
        onActiveChanged()
    }

    private var setAncestorTouchable: (Boolean) -> Unit = {
        requestDisallowInterceptTouchEvent(!it)
    }

    fun setAncestorTouchableListener(listener: (Boolean) -> Unit) {
        setAncestorTouchable = listener
    }

    fun setIndicator(indicator: BaseIndicator): BannerView {
        if (indicator.parent == null) {
            addView(indicator)
        } else if (indicator.parent != this) {
            (indicator.parent as ViewGroup).removeView(indicator)
            addView(indicator)
        }
        setOnActiveChangedListener { position, count ->
            indicator.update(position, count)
        }
        return this
    }


    fun <Item : Any> setup(adapter: BannerAdapter<Item>): BannerView {
        if (banner.adapter != adapter) {
            banner.adapter?.unregisterAdapterDataObserver(adapterDataObserver)
            banner.adapter = adapter
            adapter.registerAdapterDataObserver(adapterDataObserver)
            onActiveChanged()
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

    private fun update() {
        val running = itemCount > 1 && isVisible && isStarted && !isPaused && !isDragging
        if (running != isRunning) {
            isRunning = running
//            Log.e("OoO.banner", "running = $running")
            if (running) {
                postDelayed(runnable, interval)
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
                if (itemCount > 1) {
                    setAncestorTouchable(false)
                }
                isDragging = true
                update()
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (itemCount > 1) {
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

    private fun onActiveChanged() {
        val itemCount = banner.adapter?.itemCount ?: return

        val realCount = if (itemCount > 1) itemCount - 2 else 1
        val realPosition = when {
            itemCount <= 1 -> 0
            active == itemCount - 1 -> 0
            active == 0 -> itemCount - 2 - 1
            else -> active - 1
        }
        activeChangedListener?.onActiveChanged(realPosition, realCount)
        invalidate()
    }

    private fun reposition() {
        if (itemCount == 0) return
        val lm = banner.layoutManager ?: return
        active = lm.getPosition(pager.findSnapView(lm) ?: return)
        when (active) {
            0 -> {
                active = itemCount - 2
                scrollToPosition(active)
            }

            itemCount - 1 -> {
                active = 1
                scrollToPosition(active)
            }

            else -> {}
        }
        onActiveChanged()
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