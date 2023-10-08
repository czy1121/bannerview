package me.reezy.cosmo.bannerview.indicator

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

abstract class BaseIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr), Indicator {

    final override val style: IndicatorStyle = IndicatorStyle()

    final override val indicatorView: View get() = this

    protected var paint = Paint(Paint.ANTI_ALIAS_FLAG)

    protected var itemCount: Int = 0
        private set

    protected var activePosition: Int = 0
        private set

    init {
        paint.color = style.normalColor
    }

    final override fun onStateChanged(itemCount: Int, activePosition: Int) {
        this.itemCount = itemCount
        this.activePosition = activePosition
        requestLayout()
    }
}