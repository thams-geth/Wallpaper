package com.tts.wallpaper

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tts.wallpaper.adapter.ImageLoaderAdapter
import com.tts.wallpaper.data.ImageData
import com.tts.wallpaper.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ImageLoaderAdapter.ClickListener {

    private val CODE = 1

    private lateinit var adapter: ImageLoaderAdapter

    private lateinit var binding: ActivityMainBinding
    lateinit var imageViewModel: ImageViewModel
    private var isLoading: Boolean = false
    var handler: Handler = Handler()
    val limit: Int = 1000


    var list = ArrayList<ImageData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        imageViewModel = ViewModelProvider(this).get(ImageViewModel::class.java)

        load()

        adapter = ImageLoaderAdapter(this, list, this)
        val layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisible = layoutManager.findFirstVisibleItemPosition()
                if (!isLoading && (visibleItemCount + firstVisible) >= totalItemCount && list.size != limit) {
                    isLoading = true
                    progressBar.visibility = View.VISIBLE
                    loadMore()

                }

            }
        })

        adapter.notifyDataSetChanged()


    }

    private fun loadMore() {

        handler.postDelayed(Runnable {
            val listSize = list.size
            val nextLimit = listSize + 10
            for (i in listSize until nextLimit) {
                list.add(ImageData(i, "https://www.pixelstalk.net/wp-content/uploads/2016/09/Very-Cool-Wallpapers-HD.jpg"))
            }
            adapter.notifyDataSetChanged()
            isLoading = false
            progressBar.visibility = View.GONE

        }, 1000)
    }

    private fun load() {
        for (i in 0..9) {
            list.add(ImageData(i, "https://www.pixelstalk.net/wp-content/uploads/2016/09/Very-Cool-Wallpapers-HD.jpg"))
        }
    }


    override fun onClick(category: String) {
        val permissions = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, CODE)
        }


    }


    private fun hasPermissions(context: Context?, vararg permissions: Array<String>?): Boolean {
        if (context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission.toString()) != PackageManager.PERMISSION_GRANTED) {
                    return false
                }
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(this, ShowImageActivity::class.java))

                }
            }
        }

    }
}