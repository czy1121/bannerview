package me.reezy.cosmo.bannerview.indicator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import kotlin.math.max

/**
 * 圆形指示器
 */
class CircleIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseIndicator(context, attrs, defStyleAttr) {
    private var normalRadius: Float = 0f
    private var activeRadius: Float = 0f
    private var centerY: Float = 0f


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (itemCount <= 1) return

        normalRadius = style.normalWidth / 2f
        activeRadius = style.activeWidth / 2f
        centerY = max(activeRadius, normalRadius)

        val width: Int = (itemCount - 1) * (style.space + style.normalWidth) + style.activeWidth
        setMeasuredDimension(width, max(style.normalWidth, style.activeWidth))
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (itemCount <= 1) return

        var left = 0f
        for (i in 0 until itemCount) {
            paint.color = if (activePosition == i) style.activeColor else style.normalColor

            val width: Int = if (activePosition == i) style.activeWidth else style.normalWidth
            val radius = if (activePosition == i) activeRadius else normalRadius
            canvas.drawCircle(left + radius, centerY, radius, paint)

            left += width + style.space
        }
    }
}