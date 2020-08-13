package com.tts.wallpaper

import androidx.paging.PageKeyedDataSource
import com.tts.wallpaper.data.ImageData

class ListDataSource(private val items: List<ImageData>) : PageKeyedDataSource<Int, ImageData>() {
    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ImageData>) {

        callback.onResult(items, 0, 10)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ImageData>) {

        callback.onResult(items, params.key + 10)


    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ImageData>) {

    }
}