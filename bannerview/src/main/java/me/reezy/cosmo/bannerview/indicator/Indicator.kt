package me.reezy.cosmo.bannerview.indicator

import android.view.View

interface Indicator {
    val indicatorView: View

    val style: IndicatorStyle
    fun onStateChanged(itemCount: Int, activePosition: Int)
}