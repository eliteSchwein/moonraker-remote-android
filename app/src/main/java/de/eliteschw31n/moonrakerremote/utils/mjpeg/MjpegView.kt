package de.eliteschw31n.moonrakerremote.utils.mjpeg
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import com.chillingvan.canvasgl.ICanvasGL
import com.chillingvan.canvasgl.glcanvas.GLPaint
import com.chillingvan.canvasgl.glview.GLView

class MjpegView : GLView {
    private lateinit var thread: Thread
    private var mIn: MjpegInputStream? = null
    private var mBitmap: Bitmap? = null

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    private fun init(context: Context) {
        isFocusable = true
    }

    fun setSource(source: MjpegInputStream?) {
        mIn = source
    }

    fun stopPlayback() {
        thread.interrupt()
    }

    fun startPlayback() {
        thread = Thread {
            while (!Thread.interrupted()) {
                mBitmap = mIn?.readMjpegFrame()
                this.requestRender()
                Thread.sleep(1000)
            }
        }
        thread.start()
    }

    override fun onGLDraw(canvas: ICanvasGL?) {
        mBitmap?.let {
            canvas?.drawBitmap(it,0,0)
        }
    }
}