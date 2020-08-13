package com.tts.wallpaper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import kotlinx.android.synthetic.main.activity_show_image.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.OutputStream


class ShowImageActivity : AppCompatActivity(), View.OnTouchListener {
    var matrix = Matrix()
    var savedMatrix = Matrix()
    private var mode = NONE

    var start = PointF()
    var mid = PointF()
    var oldDist = 1f

    var path: String? = null
    private val imageFileName = "ORIGINAL" + ".jpg"
    private lateinit var storageDir: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_image)

        storageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString() + "/Wallpaper")

        Glide.with(this)
            .asBitmap()
            .load("https://www.pixelstalk.net/wp-content/uploads/2016/09/Very-Cool-Wallpapers-HD.jpg")
            .into(object : CustomTarget<Bitmap>() {

                override fun onResourceReady(res: Bitmap, transition: com.bumptech.glide.request.transition.Transition<in Bitmap>?) {
                    path = saveImage(res).toString()
//                    imageView.setImageBitmap(res)
                    saveBitmapToFile(File(path), 100, "Original")
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })

        imageView.also {
            it.setOnTouchListener(this)
        }



        btnOriginal.setOnClickListener {
            if (path != null)
                saveBitmapToFile(File(path), 100, "Original")
        }
        btnTwentyFive.setOnClickListener {
            if (path != null)
                saveBitmapToFile(File(path), 25, "25%")
        }
        btnFifty.setOnClickListener {
            if (path != null)
                saveBitmapToFile(File(path), 50, "50%")
        }
        btnSeventyFive.setOnClickListener {
            if (path != null)
                saveBitmapToFile(File(path), 90, "75%")
        }


    }


    private fun saveBitmapToFile(file: File, REQUIRED_SIZE: Int, title: String): File? {
        return try {

            // BitmapFactory options to downsize the image
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            o.inSampleSize = 6
            // factor of downsizing the image
            var inputStream = FileInputStream(file)
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o)
            inputStream.close()

            // Find the correct scale value. It should be the power of 2.
            var scale = 1
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                o.outHeight / scale / 2 >= REQUIRED_SIZE
            ) {
                scale *= 2
            }
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale
            inputStream = FileInputStream(file)
            val bitmap = BitmapFactory.decodeStream(inputStream, null, o2)
            inputStream.close()

            // here i override the original image file
            val imageFileName = "TEMP" + ".jpg"
            val imageFile = File(storageDir, imageFileName)

            imageFile.createNewFile()
            val outputStream = FileOutputStream(imageFile)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

            txtWidthheight.text = "$title \nSize : ( ${bitmap.width} x ${bitmap.height} )"
            val fileSize: Int = java.lang.String.valueOf(imageFile.length() / 1024).toInt()
            txtSize.text = " Size : $fileSize Kb"

            Glide
                .with(applicationContext)
                .load(bitmap)
                .into(imageView)

            return imageFile

        } catch (e: java.lang.Exception) {
            null
        }
    }

    private fun saveImage(image: Bitmap): String? {
        var savedImagePath: String? = null
        var success = true
        if (!storageDir.exists()) {
            success = storageDir.mkdirs()
        }
        if (success) {
            val imageFile = File(storageDir, imageFileName)
            savedImagePath = imageFile.absolutePath
            try {
                val fOut: OutputStream = FileOutputStream(imageFile)
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return savedImagePath
    }


    override fun onTouch(v: View, event: MotionEvent): Boolean {
        val view = v as ImageView
        view.scaleType = ImageView.ScaleType.MATRIX
        val scale: Float
        dumpEvent(event)
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                savedMatrix.set(matrix)
                start[event.x] = event.y
                Log.d(TAG, "mode=DRAG") // write to LogCat
                mode = DRAG
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                mode = NONE
                Log.d(TAG, "mode=NONE")
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                oldDist = spacing(event)
                Log.d(TAG, "oldDist=$oldDist")
                if (oldDist > 5f) {
                    savedMatrix.set(matrix)
                    midPoint(mid, event)
                    mode = ZOOM
                    Log.d(TAG, "mode=ZOOM")
                }
            }
            MotionEvent.ACTION_MOVE -> if (mode == DRAG) {
                matrix.set(savedMatrix)
                matrix.postTranslate(event.x - start.x, event.y - start.y) // create the transformation in the matrix  of points
            } else if (mode == ZOOM) {
                // pinch zooming
                val newDist = spacing(event)
                Log.d(TAG, "newDist=$newDist")
                if (newDist > 5f) {
                    matrix.set(savedMatrix)
                    scale = newDist / oldDist // setting the scaling of the
                    matrix.postScale(scale, scale, mid.x, mid.y)
                }
            }
        }
        view.imageMatrix = matrix // display the transformation on screen
        return true // indicate event was handled
    }

    private fun spacing(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return Math.sqrt(x * x + y * y.toDouble()).toFloat()
    }

    private fun midPoint(point: PointF, event: MotionEvent) {
        val x = event.getX(0) + event.getX(1)
        val y = event.getY(0) + event.getY(1)
        point[x / 2] = y / 2
    }

    /** Show an event in the LogCat view, for debugging  */
    private fun dumpEvent(event: MotionEvent) {
        val names = arrayOf("DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?")
        val sb = StringBuilder()
        val action = event.action
        val actionCode = action and MotionEvent.ACTION_MASK
        sb.append("event ACTION_").append(names[actionCode])
        if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP) {
            sb.append("(pid ").append(action shr MotionEvent.ACTION_POINTER_ID_SHIFT)
            sb.append(")")
        }
        sb.append("[")
        for (i in 0 until event.pointerCount) {
            sb.append("#").append(i)
            sb.append("(pid ").append(event.getPointerId(i))
            sb.append(")=").append(event.getX(i).toInt())
            sb.append(",").append(event.getY(i).toInt())
            if (i + 1 < event.pointerCount) sb.append(";")
        }
        sb.append("]")
        Log.d("Touch Events ---------", sb.toString())
    }

    companion object {
        private const val TAG = "Touch"

        const val NONE = 0
        const val DRAG = 1
        const val ZOOM = 2
    }
}