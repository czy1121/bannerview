package com.demo.app

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.reezy.cosmo.bannerview.BannerView
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

        banner.setIndicator(RectangleIndicator(this))

        banner.setup(adapter).bind(this).start()


        adapter.submitList(listOf("One", "Two", "Three", "Four", "Five", "Six"))

    }
}