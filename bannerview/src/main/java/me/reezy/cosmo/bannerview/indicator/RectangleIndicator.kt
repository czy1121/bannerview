package me.reezy.cosmo.bannerview.indicator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

class RectangleIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseIndicator(context, attrs, defStyleAttr) {


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (count <= 1) return
        setMeasuredDimension((count - 1) * (style.gap + style.width) + style.activeWidth, style.height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (count <= 1) return
        val r = style.radius.toFloat()
        var left = 0f
        for (i in 0 until count) {
            paint.color = if (active == i) style.activeColor else style.color

            val itemWidth = if (active == i) style.activeWidth else style.width

            canvas.drawRoundRect(left, 0f, left + itemWidth, style.height.toFloat(), r, r, paint)

            left += itemWidth + style.gap
        }
    }
}