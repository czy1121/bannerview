package me.reezy.cosmo.bannerview.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

abstract class ImageBannerAdapter<Item : Any> : BannerAdapter<Item>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(ImageView(parent.context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.MATCH_PARENT)
        })
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        onBind(holder.itemView as ImageView, getItem(position))
    }

    abstract fun onBind(view: ImageView, item: Item)
}