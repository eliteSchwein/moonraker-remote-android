package de.eliteschw31n.moonrakerremote.ui.webcam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase
import org.json.JSONObject

class WebcamFragment : Fragment() {
    private lateinit var printerData: JSONObject
    private lateinit var currentPrinter: String

    private lateinit var webcamViewModel: WebcamViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        webcamViewModel =
                ViewModelProvider(this).get(WebcamViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_webcam, container, false)

        currentPrinter = LocalDatabase.getData().getString("currentPrinter")
        printerData = LocalDatabase.getPrinterData(currentPrinter)

        val webcamStream: WebView = root.findViewById(R.id.webcam_view)
        webcamStream.loadUrl(printerData.getString("webcamurl"))

        webcamStream.settings.loadWithOverviewMode = true
        webcamStream.settings.useWideViewPort = true
        webcamStream.settings.builtInZoomControls = true
        webcamStream.settings.displayZoomControls = false

        webcamStream.setInitialScale(1)
        webcamStream.setPadding(0, 0, 0, 0)

        webcamStream.webViewClient = object: WebViewClient() {
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                webcamStream.settings.builtInZoomControls = false
                webcamStream.loadUrl("file:///android_asset/webcam_error.html")
            }
        }

        return root
    }
}