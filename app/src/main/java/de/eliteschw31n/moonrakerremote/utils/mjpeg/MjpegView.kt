package de.eliteschw31n.moonrakerremote.utils.mjpeg

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.chillingvan.canvasgl.ICanvasGL
import com.chillingvan.canvasgl.glcanvas.GLPaint
import com.chillingvan.canvasgl.glview.GLView
import de.eliteschw31n.moonrakerremote.MainActivity
import java.io.IOException


class MjpegView : GLView {
    private var thread: MjpegViewThread? = null
    private var mIn: MjpegInputStream? = null
    private var canvas: ICanvasGL? = null
    private var mRun = false

    inner class MjpegViewThread(context: Context?) : Thread() {
        override fun run() {
            var bm: Bitmap
            while (mRun) {
                while (canvas == null) { }
                try {
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun init(context: Context) {
        isFocusable = true
    }

    fun startPlayback() {
        if (mIn != null) {
            mRun = true
            thread = MjpegViewThread(context)
            Log.d("stream", mIn.toString())
            thread?.start()
        }
    }

    fun stopPlayback() {
        mRun = false
        var retry = true
        while (retry) {
            try {
                thread?.join()
                retry = false
            } catch (e: InterruptedException) {
            }
        }
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    override fun onGLDraw(canvas: ICanvasGL?) {
        Log.d("framework", canvas.toString())
        if (canvas != null) {
            this.canvas = canvas
            Thread {
                while (mIn == null) { Log.d("null", "null")}
                try {
                    val bm = mIn!!.readMjpegFrame()
                    canvas.drawBitmap(bm, 0, 0)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }.start()
            //thread?.join()
        }
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    fun setSource(source: MjpegInputStream?) {
        mIn = source
    }

    companion object {
    }
}