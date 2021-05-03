package de.eliteschw31n.moonrakerremote.ui.webcam

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase
import de.eliteschw31n.moonrakerremote.utils.WebcamUtil
import de.eliteschw31n.moonrakerremote.utils.mjpeg.Mjpeg2View
import de.eliteschw31n.moonrakerremote.utils.mjpeg.MjpegInputStream
import de.eliteschw31n.moonrakerremote.utils.mjpeg.MjpegView
import org.json.JSONObject
import java.util.*


class WebcamFragment : Fragment() {
    private lateinit var printerData: JSONObject
    private lateinit var currentPrinter: String
    private lateinit var webcamView: WebView

    override fun onDestroy() {
        super.onDestroy()
        //mjpegView3.stopPlayback()
        webcamView.destroy()
        //mjpegView.stopPlayback()
        //mjpegView2.stopStream()
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_webcam, container, false)

        currentPrinter = LocalDatabase.getData().getString("currentPrinter")
        printerData = LocalDatabase.getPrinterData(currentPrinter)

        webcamView = root.findViewById(R.id.webcam_view)

        WebcamUtil.preloadWebcam(webcamView)

        WebcamUtil.loadWebcam(true, printerData.getString("webcamurl"), webcamView)

        return root
    }
}