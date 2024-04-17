package com.demo.app

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil.load
import me.reezy.cosmo.bannerview.BannerView
import me.reezy.cosmo.bannerview.adapter.ImageBannerAdapter
import me.reezy.cosmo.bannerview.indicator.Indicator
import me.reezy.cosmo.bannerview.indicator.IndicatorStyle
import me.reezy.cosmo.bannerview.indicator.NumberIndicator
import me.reezy.cosmo.bannerview.indicator.RectangleIndicator

class MainActivity : AppCompatActivity() {

    private val adapter = DemoBannerAdapter()

//    private val adapter = object : ImageBannerAdapter<String>() {
//        override fun onBind(view: ImageView, item: String) {
//            view.load(item)
//        }
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        val banner = findViewById<BannerView>(R.id.banner)
        banner.setBackgroundColor(Color.RED)

//        banner.indicator = RectangleIndicator(this)

        banner.indicator = NumberIndicator(this).apply {
            val pad = (10 * resources.displayMetrics.density).toInt()
            setPadding(pad, pad, pad, pad)
            setBackgroundColor(Color.BLACK)
            setTextColor(Color.WHITE)
        }

        banner.setup(adapter).bind(this).start()

        adapter.submitList(listOf("One"))

        findViewById<View>(android.R.id.content).setOnClickListener {
            Log.e("OoO", "submitList")
            adapter.submitList(listOf("One", "Two", "Three", "Four", "Five", "Six"))
        }

    }
}