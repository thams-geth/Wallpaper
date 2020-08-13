package com.tts.wallpaper

import com.tts.wallpaper.data.ImageData


class ImageRepository() {
    var list = ArrayList<ImageData>()

    fun getCurrencyRates(base: String): List<ImageData> {
        return list
    }

    fun load() {

        for (i in 1..100) {
            list.add(ImageData(i, "https://www.pixelstalk.net/wp-content/uploads/2016/09/Very-Cool-Wallpapers-HD.jpg"))
        }
    }

}