package de.eliteschw31n.moonrakerremote.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase

class SettingsFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        val printers = LocalDatabase.getData().getJSONObject("printers")
        val currentPrinter = LocalDatabase.getData().getString("currentPrinter")
        val currentPrinterJson = printers.getJSONObject(currentPrinter)

        val nameTextLayout: TextInputLayout = root.findViewById(R.id.input_settings_connection_name)
        val nameTextEdit = nameTextLayout.editText
        nameTextEdit?.setText(currentPrinter)

        val websocketTextLayout: TextInputLayout = root.findViewById(R.id.input_settings_connection_websocket)
        val websocketTextEdit = websocketTextLayout.editText
        websocketTextEdit?.setText(currentPrinterJson.getString("websocketurl"))

        val webcamTextLayout: TextInputLayout = root.findViewById(R.id.input_settings_connection_stream_url)
        val webcamTextEdit = webcamTextLayout.editText
        webcamTextEdit?.setText(currentPrinterJson.getString("webcamurl"))

        return root
    }
}