package com.tts.wallpaper.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tts.wallpaper.R
import com.tts.wallpaper.data.ImageData
import kotlinx.android.synthetic.main.item_imageloader.view.*

class ImageLoaderAdapterPaged(private val context: Context, var clickListener: ClickListener) :
    PagedListAdapter<ImageData, RecyclerView.ViewHolder>(ImageDiffCallback()) {

    class ImageDiffCallback : DiffUtil.ItemCallback<ImageData>() {
        override fun areItemsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ImageData, newItem: ImageData): Boolean {
            return oldItem == newItem
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_imageloader, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolder).bind(getItem(position), context)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(imageData: ImageData?, context: Context) {
            Glide.with(context).load(imageData?.url).into(itemView.imageView)

            itemView.setOnClickListener {
//                val intent:Intent()
            }
        }
    }


    interface ClickListener {
        fun onClick(category: String)
    }
}