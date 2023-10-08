package me.reezy.cosmo.bannerview.indicator

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet

class RoundLineIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseIndicator(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (itemCount <= 1) return
        setMeasuredDimension(itemCount * style.activeWidth, style.height)
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (itemCount <= 1) return

        val radius = style.radius.toFloat()
        val height = style.height.toFloat()

        paint.color = style.normalColor
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height, radius, radius, paint)

        paint.color = style.activeColor
        val left = activePosition * style.activeWidth.toFloat()
        val right = left + style.activeWidth.toFloat()
        canvas.drawRoundRect(left, 0f, right, height, radius, radius, paint)
    }
}