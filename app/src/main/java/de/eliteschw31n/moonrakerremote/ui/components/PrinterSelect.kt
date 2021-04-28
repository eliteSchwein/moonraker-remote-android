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
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.eliteschw31n.moonrakerremote.MainActivity
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase
import de.eliteschw31n.moonrakerremote.utils.NavTitles
import de.eliteschw31n.moonrakerremote.utils.Theme
import org.json.JSONObject

class PrinterSelect : Fragment()  {

    private lateinit var profileData: JSONObject
    private lateinit var cameraPreview: WebView
    private lateinit var editButton: FloatingActionButton
    private lateinit var clickArea: View
    private lateinit var title: TextView

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.component_printer_select, container, false)

        title = root.findViewById(R.id.component_printer_select_title)
        cameraPreview = root.findViewById(R.id.webcam_preview)
        editButton = root.findViewById(R.id.component_printer_select_edit)
        clickArea = root.findViewById(R.id.component_printer_select_click_area)

        when (Theme.isDarkMode()) {
            true -> { cameraPreview.loadUrl("file:///android_asset/webcam_preview_loading.html") }
            false -> { cameraPreview.loadUrl("file:///android_asset/webcam_preview_loading_light.html") }
        }

        Thread {
            while (title.text == "") {
                Thread.sleep(1000)
            }
            profileData = LocalDatabase.getPrinterData(title.text.toString())
            MainActivity.runUiUpdate(Runnable {
                handleClickArea()
                handleEditButton()
                loadPreview()
                if(LocalDatabase.getData().getString("currentPrinter") == title.text.toString()) {
                    clickArea.isClickable = false
                    clickArea.setBackgroundResource(R.color.transparent_grey_500)
                }
            })
        }.start()
        return root
    }

    private fun handleClickArea() {
        clickArea.setOnClickListener {
            val currentPrinter = LocalDatabase.getData().getString("currentPrinter")
            val currentFragment = MainActivity.supportFragmentManager().findFragmentByTag("printer_profile_$currentPrinter")
            val currentClickArea: View? = currentFragment?.view?.findViewById(R.id.component_printer_select_click_area)
            LocalDatabase.updateCurrentPrinter(title.text.toString())
            NavTitles.updateTitles()
            MainActivity.runUiUpdate(Runnable {
                currentClickArea?.isClickable = true
                currentClickArea?.setBackgroundResource(R.color.transparent)
                clickArea.isClickable = false
                clickArea.setBackgroundResource(R.color.transparent_grey_500)
            })
        }
    }

    private fun handleEditButton() {
        editButton.setOnClickListener {
            LocalDatabase.setCurrentEditPrinter(title.text.toString())
            findNavController().navigate(R.id.nav_printer_settings)
        }
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
                when (Theme.isDarkMode()) {
                    true -> { cameraPreview.loadUrl("file:///android_asset/webcam_preview_error.html") }
                    false -> { cameraPreview.loadUrl("file:///android_asset/webcam_preview_error_light.html") }
                }
            }
        }
    }
}