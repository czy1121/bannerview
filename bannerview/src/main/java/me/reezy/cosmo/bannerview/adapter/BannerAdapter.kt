package me.reezy.cosmo.bannerview.adapter

import android.annotation.SuppressLint
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BannerAdapter<Item : Any>() : RecyclerView.Adapter<BannerAdapter.BannerViewHolder>() {

    class BannerViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private var list: List<Item> = listOf()

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(data: List<Item>) {
        if (data.isEmpty()) return
        list = if (data.size > 1) (listOf(data[data.size - 1]) + data + data[0]) else data
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = list.size

    fun getItem(position: Int): Item = list[position]
}