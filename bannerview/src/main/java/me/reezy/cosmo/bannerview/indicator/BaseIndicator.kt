package me.reezy.cosmo.bannerview.indicator

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.setMargins

abstract class BaseIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    val style: IndicatorStyle = IndicatorStyle()

    protected var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    protected var count: Int = 0
        private set

    protected var active: Int = 0
        private set

    init {
        paint.color = style.normalColor

        val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        lp.setMargins((resources.displayMetrics.density * 20).toInt())
        lp.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        layoutParams = lp
    }


    fun update(active: Int, count: Int) {
        this.active = active
        this.count = count
        requestLayout()
    }
}