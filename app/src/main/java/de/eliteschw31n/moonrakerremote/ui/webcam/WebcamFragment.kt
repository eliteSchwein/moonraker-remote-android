package de.eliteschw31n.moonrakerremote.ui.webcam

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import de.eliteschw31n.moonrakerremote.MainActivity
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase
import de.eliteschw31n.moonrakerremote.utils.Theme
import de.eliteschw31n.moonrakerremote.utils.mjpeg.MjpegInputStream
import de.eliteschw31n.moonrakerremote.utils.mjpeg.MjpegView
import org.json.JSONObject
import java.io.InputStream

class WebcamFragment : Fragment() {
    private lateinit var printerData: JSONObject
    private lateinit var currentPrinter: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_webcam, container, false)

        currentPrinter = LocalDatabase.getData().getString("currentPrinter")
        printerData = LocalDatabase.getPrinterData(currentPrinter)

        val webcamStream: WebView = root.findViewById(R.id.webcam_view)
        webcamStream.loadUrl(printerData.getString("webcamurl"))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // chromium, enable hardware acceleration
            webcamStream.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            webcamStream.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        val webcamView: MjpegView = root.findViewById(R.id.webcam_stream)
        webcamView.setDisplayMode(MjpegView.SIZE_BEST_FIT)
        webcamView.showFps(true)
        webcamView.setSource(MjpegInputStream.read(printerData.getString("webcamurl")))
        webcamView.startPlayback()

        webcamStream.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

        webcamStream.settings.javaScriptEnabled = true
        webcamStream.settings.cacheMode = WebSettings.LOAD_NO_CACHE
        webcamStream.settings.loadWithOverviewMode = true
        webcamStream.settings.useWideViewPort = true
        webcamStream.settings.builtInZoomControls = true
        webcamStream.settings.displayZoomControls = false

        webcamStream.setInitialScale(1)
        webcamStream.setPadding(0, 0, 0, 0)
        webcamStream.webViewClient = object: WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                errorCode: Int,
                description: String,
                failingURL: String
            ) {
                webcamStream.settings.builtInZoomControls = false
                when (Theme.isDarkMode()) {
                    true -> {
                        webcamStream.loadUrl("file:///android_asset/webcam_error.html")
                    }
                    false -> {
                        webcamStream.loadUrl("file:///android_asset/webcam_error_light.html")
                    }
                }
            }
        }

        return root
    }
}