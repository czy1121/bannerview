package com.demo.app

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import me.reezy.cosmo.bannerview.BannerView

class BannerAdapter : BannerView.Adapter<String>() {

    //    private val colors = arrayOf(Color.BLUE, Color.RED, Color.MAGENTA, Color.GREEN, Color.DKGRAY, Color.CYAN, Color.YELLOW)
    private val colors = arrayOf(Color.DKGRAY)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerView.ViewHolder {
        return BannerView.ViewHolder(TextView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
        })
    }

    override fun onBindViewHolder(holder: BannerView.ViewHolder, position: Int) {
        (holder.itemView as TextView).apply {
            @SuppressLint("SetTextI18n")
            val origin = getOriginPosition(position)
            text = "${getItem(position)}[$origin]"
            setBackgroundColor(colors[origin % colors.size])
        }
    }
}