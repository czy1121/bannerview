package com.demo.app

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.demo.app.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val binding by lazy { ActivityMainBinding.bind((findViewById<ViewGroup>(android.R.id.content)).getChildAt(0)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.banner.setAdapter(BannerAdapter()).bind(this).start()

        binding.banner.setData(listOf("One", "Two", "Three", "Four", "Five", "Six"))

    }
}