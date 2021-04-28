package de.eliteschw31n.moonrakerremote.ui.components

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.eliteschw31n.moonrakerremote.MainActivity
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase
import org.json.JSONObject

class PrinterSelect : Fragment()  {

    private lateinit var profileData: JSONObject
    private lateinit var cameraPreview: WebView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.component_printer_select, container, false)
        val title: TextView = root.findViewById(R.id.component_printer_select_title)
        cameraPreview = root.findViewById(R.id.webcam_preview)
        val editButton: FloatingActionButton = root.findViewById(R.id.component_printer_select_edit)
        Thread {
            while (title.text == "") { }
            profileData = LocalDatabase.getPrinterData(title.text.toString())
            MainActivity.runUiUpdate(Runnable {
                loadPreview()
            })
        }.start()
        return root
    }

    private fun loadPreview() {

        cameraPreview.loadUrl(profileData.getString("webcamurl"))

        cameraPreview.settings.loadWithOverviewMode = true
        cameraPreview.settings.useWideViewPort = true
        cameraPreview.settings.builtInZoomControls = false
        cameraPreview.settings.displayZoomControls = false

        cameraPreview.setInitialScale(1)
        cameraPreview.setPadding(0, 0, 0, 0)

        cameraPreview.webViewClient = object: WebViewClient() {
            override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
            ) {
                cameraPreview.loadUrl("file:///android_asset/webcam_preview_error.html")
            }
        }
    }
}