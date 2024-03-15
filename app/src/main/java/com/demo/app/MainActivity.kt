package com.demo.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.reezy.cosmo.bannerview.BannerView
import me.reezy.cosmo.bannerview.indicator.RectangleIndicator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val adapter = DemoBannerAdapter()

        val banner = findViewById<BannerView>(R.id.banner)

        banner.indicator = RectangleIndicator(this)

        banner.aspectRatio = 1f

        banner.setup(adapter).bind(this).start()

        banner.postDelayed({
            adapter.submitList(listOf("One"))//, "Two", "Three", "Four", "Five", "Six"))
        }, 1000)





    }
}