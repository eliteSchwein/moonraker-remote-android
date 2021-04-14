package de.eliteschw31n.moonrakerremote.ui.settings

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import de.eliteschw31n.moonrakerremote.MainActivity
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase
import de.eliteschw31n.moonrakerremote.utils.NavTitles
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import java.util.*

class SettingsFragment : Fragment() {
    private lateinit var webSocketClient: WebSocketClient
    private lateinit var printerData: JSONObject
    private lateinit var currentPrinter: String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        currentPrinter = LocalDatabase.getData().getString("currentPrinter")
        printerData = LocalDatabase.getPrinterData(currentPrinter)

        val nameTextLayout: TextInputLayout = root.findViewById(R.id.input_settings_connection_name)
        val nameTextEdit = nameTextLayout.editText
        nameTextEdit?.setText(currentPrinter)

        val websocketTextLayout: TextInputLayout = root.findViewById(R.id.input_settings_connection_websocket)
        val websocketTextEdit = websocketTextLayout.editText
        websocketTextEdit?.setText(printerData.getString("websocketurl"))
        websocketTextLayout.setEndIconOnClickListener {
            val websocketURL = websocketTextEdit?.text.toString()
            updateWebsocket(websocketURL, websocketTextLayout)
        }

        val webcamTextLayout: TextInputLayout = root.findViewById(R.id.input_settings_connection_stream_url)
        val webcamTextEdit = webcamTextLayout.editText

        webcamTextEdit?.setText(printerData.getString("webcamurl"))

        return root
    }
    private fun setError(textlayout: TextInputLayout, errorReason: String){
        MainActivity.runUiUpdate(Runnable {
            textlayout.error = errorReason
            textlayout.isErrorEnabled = true
        })
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.runUiUpdate(Runnable {
                    textlayout.error = null
                    textlayout.isErrorEnabled = false
                })
            }
        }, 2000)
    }
    private fun savePrinterData(key: String, data: Any) {
        printerData = LocalDatabase.getPrinterData(currentPrinter)

        printerData.put(key, data)
        LocalDatabase.updatePrinterData(currentPrinter, printerData)

        NavTitles.updateSubTitle(printerData.getString("websocketurl"))
        NavTitles.updateTitle(currentPrinter)
    }
    private fun toggleInputEdit(textlayout: TextInputLayout){
        MainActivity.runUiUpdate(Runnable {
            if(textlayout.editText?.isEnabled == true){
                textlayout.editText?.isEnabled = false
                return@Runnable
            }
            textlayout.editText?.isEnabled = true
        })
    }
    private fun updateWebsocket(url: String, textlayout: TextInputLayout) {
        var validated = false
        toggleInputEdit(textlayout)
        webSocketClient = object : WebSocketClient(URI(url)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                toggleInputEdit(textlayout)
                savePrinterData("websocketurl", url)
                validated = true
                close()
            }

            override fun onMessage(message: String?) {
                toggleInputEdit(textlayout)
                savePrinterData("websocketurl", url)
                validated = true
                close()
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                if(!validated){
                    toggleInputEdit(textlayout)
                    setError(textlayout, "Invalid URL!")
                }
            }

            override fun onError(ex: Exception?) {
                toggleInputEdit(textlayout)
                setError(textlayout, "Invalid URL!")
                validated = true
            }
        }
        webSocketClient.connect()
    }
}