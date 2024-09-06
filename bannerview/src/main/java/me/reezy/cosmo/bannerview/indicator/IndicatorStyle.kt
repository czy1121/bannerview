package me.reezy.cosmo.bannerview.indicator

import android.content.res.Resources
import androidx.annotation.ColorInt

class IndicatorStyle {
    var gap: Int = 5.dp
        private set

    var radius: Int = 3.dp
        private set

    var width: Int = 5.dp
        private set
    var activeWidth: Int = 7.dp
        private set


    var height: Int = 3.dp
        private set
    var activeHeight: Int = 7.dp
        private set

    @ColorInt
    var color: Int = (0x80ffffff).toInt()
        private set

    @ColorInt
    var activeColor: Int = (0x80000000).toInt()
        private set

    fun setColor(color: Int, activeColor: Int = color): IndicatorStyle {
        this.color = color
        this.activeColor = activeColor
        return this
    }

    fun setGap(gap: Int): IndicatorStyle {
        this.gap = gap
        return this
    }

    fun setSize(size: Int, activeSize: Int = size): IndicatorStyle {
        this.width = size
        this.height = size
        this.activeWidth = activeSize
        this.activeHeight = activeSize
        return this
    }

    fun setHeight(height: Int, activeHeight: Int = height): IndicatorStyle {
        this.height = height
        this.activeHeight = activeHeight
        return this
    }

    fun setWidth(width: Int, activeWidth: Int = width): IndicatorStyle {
        this.width = activeWidth
        this.activeWidth = activeWidth
        return this
    }

    fun setRadius(radius: Int): IndicatorStyle {
        this.radius = radius
        return this
    }

    companion object {
        private val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}