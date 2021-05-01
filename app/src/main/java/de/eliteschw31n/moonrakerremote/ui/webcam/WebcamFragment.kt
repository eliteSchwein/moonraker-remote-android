package de.eliteschw31n.moonrakerremote.ui.webcam

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import androidx.fragment.app.Fragment
import com.longdo.mjpegviewer.MjpegView
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase
import de.eliteschw31n.moonrakerremote.utils.WebcamUtil
import org.json.JSONObject
import java.util.*


class WebcamFragment : Fragment() {
    private lateinit var printerData: JSONObject
    private lateinit var currentPrinter: String
    private lateinit var webcamView: WebView

    override fun onDestroy() {
        super.onDestroy()
        webcamView.destroy()
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

        WebcamUtil.loadWebcam(false, printerData.getString("webcamurl"), webcamView)

        return root
    }
}