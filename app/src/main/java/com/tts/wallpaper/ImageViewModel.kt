package com.tts.wallpaper

import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.tts.wallpaper.data.ImageData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.Executor
import kotlin.collections.ArrayList

class ImageViewModel : ViewModel() {

    val mutableLiveData: MutableLiveData<PagedList<ImageData>> = MutableLiveData<PagedList<ImageData>>()
    var stateLiveData: LiveData<PagedList<ImageData>> = mutableLiveData
    var list = ArrayList<ImageData>()



    fun onRefresh() {

        val imageDataObject = ImageData(1, "https://www.pixelstalk.net/wp-content/uploads/2016/09/Very-Cool-Wallpapers-HD.jpg")
//        val list: MutableList<ImageData> = Collections.nCopies(50, imageDataObject)
//        val list = MutableList<ImageData>(500)

        for (i in 1..100) {
            list.add(ImageData(i, "https://www.pixelstalk.net/wp-content/uploads/2016/09/Very-Cool-Wallpapers-HD.jpg"))
        }

        val config = PagedList.Config.Builder()
            .setPageSize(10)
            .setEnablePlaceholders(false)
//            .setInitialLoadSizeHint(5)
            .build()

        val pagedList = PagedList.Builder(ListDataSource(list), config)
            .setNotifyExecutor(UiThreadExecutor())
            .setFetchExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            .build()
//        val l :LiveData<PagedList<ImageData>> = LivePagedListBuilder(ListDataSource(list),config)

        mutableLiveData.value = pagedList


    }


}

class UiThreadExecutor : Executor {
    private val handler = Handler(Looper.getMainLooper())
    override fun execute(command: Runnable) {
        handler.post(command)
    }
}