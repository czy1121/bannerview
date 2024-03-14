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
        adapter.submitList(listOf("One", "Two", "Three", "Four", "Five", "Six"))

        val banner = findViewById<BannerView>(R.id.banner)

        banner.indicator = RectangleIndicator(this)

        banner.aspectRatio = 2f

        banner.setup(adapter).bind(this).start()




    }
}