package me.reezy.cosmo.bannerview.indicator

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.widget.TextViewCompat

class NumberIndicator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AppCompatTextView(context, attrs, defStyleAttr), Indicator {

    init {
        gravity = Gravity.CENTER
        textSize = 16f
    }

    override val indicatorView: View = this

    override val style: IndicatorStyle = IndicatorStyle()

    override fun onStateChanged(itemCount: Int, activePosition: Int) {
        text = "${activePosition + 1}/$itemCount"
    }

}