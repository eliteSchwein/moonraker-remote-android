package de.eliteschw31n.moonrakerremote.utils.mjpeg
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import com.chillingvan.canvasgl.ICanvasGL
import com.chillingvan.canvasgl.glcanvas.GLPaint
import com.chillingvan.canvasgl.glview.GLView

class MjpegView : GLView {
    private lateinit var renderThread: Thread
    private lateinit var webThread: Thread
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
        webThread.interrupt()
        renderThread.interrupt()
    }

    fun startPlayback() {
        renderThread = Thread {
            while (!Thread.interrupted()) {
                this.requestRender()
                Thread.sleep(250)
            }
        }
        renderThread.start()
        webThread = Thread {
            while (!Thread.interrupted()) {
                mBitmap = mIn?.readMjpegFrame()
            }
        }
        webThread.start()
    }

    override fun onGLDraw(canvas: ICanvasGL?) {
        mBitmap?.let {
            canvas?.drawBitmap(it,0,0)
        }
    }
}