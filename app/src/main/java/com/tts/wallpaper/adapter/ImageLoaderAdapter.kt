package com.tts.wallpaper.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tts.wallpaper.R
import com.tts.wallpaper.data.ImageData
import kotlinx.android.synthetic.main.item_imageloader.view.*


class ImageLoaderAdapter(var context: Context, var list: List<ImageData>, var clickListener: ClickListener) :
    RecyclerView.Adapter<ImageLoaderAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_imageloader, parent, false))

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        Glide.with(context).load(list[position].url).into(holder.itemView.imageView)
        Glide
            .with(context)
            .load(list[position].url)
            .placeholder(R.drawable.loading)
//            .fitCenter()
            .into(holder.itemView.imageView)

        holder.itemView.setOnClickListener {
            clickListener.onClick(list[position].toString())

        }


    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    interface ClickListener {
        fun onClick(category: String)
    }
}