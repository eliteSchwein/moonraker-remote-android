package de.eliteschw31n.moonrakerremote.utils

import android.os.Build
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import de.eliteschw31n.moonrakerremote.MainActivity

class WebcamUtil {
    companion object {
        fun preloadWebcam(webcamView: WebView) {
            configureWebView(webcamView, false)

            MainActivity.runUiUpdate(Runnable {
                when (Theme.isDarkMode()) {
                    true -> { webcamView.loadUrl("file:///android_asset/webcam_preview_loading.html") }
                    false -> { webcamView.loadUrl("file:///android_asset/webcam_preview_loading_light.html") }
                }
            })
        }
        fun loadWebcam(enableZoom: Boolean, URL:String, webcamView: WebView) {
            configureWebView(webcamView, enableZoom)

            Thread {
                Thread.sleep(500)
                MainActivity.runUiUpdate(Runnable {
                    webcamView.loadUrl(URL)
                })
            }.start()
        }
        private fun configureWebView(webcamView: WebView, enableZoom: Boolean) {
            webcamView.settings.setRenderPriority(WebSettings.RenderPriority.HIGH)

            webcamView.settings.javaScriptEnabled = true
            webcamView.settings.cacheMode = WebSettings.LOAD_NO_CACHE
            webcamView.settings.builtInZoomControls = enableZoom
            webcamView.settings.displayZoomControls = false
            webcamView.settings.loadWithOverviewMode = true
            webcamView.settings.useWideViewPort = true

            webcamView.isVerticalScrollBarEnabled = enableZoom
            webcamView.isHorizontalScrollBarEnabled = enableZoom

            webcamView.setInitialScale(1)
            webcamView.setPadding(0, 0, 0, 0)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webcamView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            } else {
                webcamView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            }

            webcamView.webViewClient = object: WebViewClient() {
                override fun onReceivedError(
                        view: WebView?,
                        errorCode: Int,
                        description: String,
                        failingURL: String
                ) {
                    when (Theme.isDarkMode()) {
                        true -> { webcamView.loadUrl("file:///android_asset/webcam_error.html") }
                        false -> { webcamView.loadUrl("file:///android_asset/webcam_error_light.html") }
                    }
                }
            }
            webcamView.setOnTouchListener { v, event ->
                !enableZoom
            }
        }
    }
}