package me.reezy.cosmo.bannerview.indicator

import android.content.res.Resources
import android.graphics.Rect
import android.widget.FrameLayout.LayoutParams
import androidx.annotation.ColorInt
import androidx.annotation.IntDef

class IndicatorStyle {
    var gravity = Gravity.CENTER
        private set
    var space: Int = 5.dp
        private set
    var height: Int = 3.dp
        private set
    var normalWidth: Int = 5.dp
        private set
    var activeWidth: Int = 7.dp
        private set
    var radius: Int = 3.dp
        private set

    @ColorInt
    var normalColor: Int = (0x80ffffff).toInt()
        private set

    @ColorInt
    var activeColor: Int = (0x80000000).toInt()
        private set

    fun setNormalColor(normalColor: Int): IndicatorStyle {
        this.normalColor = normalColor
        return this
    }

    fun setActiveColor(selectedColor: Int): IndicatorStyle {
        this.activeColor = selectedColor
        return this
    }

    fun setGravity(@Gravity gravity: Int): IndicatorStyle {
        this.gravity = gravity
        return this
    }

    fun setSpace(indicatorSpace: Int): IndicatorStyle {
        this.space = indicatorSpace
        return this
    }

    fun setHeight(height: Int): IndicatorStyle {
        this.height = height
        return this
    }

    fun setNormalWidth(normalWidth: Int): IndicatorStyle {
        this.normalWidth = normalWidth
        return this
    }

    fun setActiveWidth(activeWidth: Int): IndicatorStyle {
        this.activeWidth = activeWidth
        return this
    }

    fun setRadius(radius: Int): IndicatorStyle {
        this.radius = radius
        return this
    }


    @IntDef(Gravity.START, Gravity.CENTER, Gravity.END)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Gravity {
        companion object {
            const val START = 0
            const val CENTER = 1
            const val END = 2
        }
    }

    companion object {
        private val Int.dp: Int get() = (this * Resources.getSystem().displayMetrics.density).toInt()
    }
}