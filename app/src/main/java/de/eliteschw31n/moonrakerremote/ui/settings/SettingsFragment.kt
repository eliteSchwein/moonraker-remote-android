package de.eliteschw31n.moonrakerremote.ui.settings

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.NetworkOnMainThreadException
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
import java.io.BufferedInputStream
import java.io.IOException
import java.net.*
import java.util.*

class SettingsFragment : Fragment() {
    private lateinit var webSocketClient: WebSocketClient
    private lateinit var printerData: JSONObject
    private lateinit var printerProfiles: JSONObject
    private lateinit var currentPrinter: String

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        currentPrinter = LocalDatabase.getData().getString("currentPrinter")
        printerData = LocalDatabase.getPrinterData(currentPrinter)
        printerProfiles = LocalDatabase.getData().getJSONObject("printers")

        val nameTextLayout: TextInputLayout = root.findViewById(R.id.input_settings_connection_name)
        val nameTextEdit = nameTextLayout.editText
        nameTextEdit?.setText(currentPrinter)
        nameTextLayout.setEndIconOnClickListener {
            val profileName = nameTextEdit?.text.toString()
            if(profileName.isNullOrBlank() || profileName.isNullOrEmpty()){
                setError(nameTextLayout, "Empty!")
                return@setEndIconOnClickListener
            }
            if(profileName.length > 64) {
                setError(nameTextLayout, "Too long (max: 64)!")
                return@setEndIconOnClickListener
            }
            printerProfiles.keys().forEach {
                val profile = it
                Log.d("profile", profile)
                if(profileName == profile){
                    setError(nameTextLayout, "Name already in use!")
                    return@setEndIconOnClickListener
                }
            }
            replaceName(profileName)
            setSaved(nameTextLayout, "Saved new Profile Name!")
        }

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
        webcamTextLayout.setEndIconOnClickListener {
            val webcamURL = webcamTextEdit?.text.toString()
            updateWebcamUrl(webcamURL, webcamTextLayout)
        }

        return root
    }
    private fun replaceName(name:String) {
        printerProfiles.remove(currentPrinter)
        printerProfiles.put(name, printerData)
        val database = LocalDatabase.getData()
        database.put("printers", printerProfiles)
        database.put("currentPrinter", name)
        LocalDatabase.writeData(database)
        currentPrinter = name
        updateNavTitles()
    }
    private fun setSaved(textlayout: TextInputLayout, saveMessage: String) {
        MainActivity.runUiUpdate(Runnable {
            textlayout.helperText = saveMessage
            textlayout.isHelperTextEnabled = true
        })
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.runUiUpdate(Runnable {
                    textlayout.helperText = null
                    textlayout.isHelperTextEnabled = false
                })
            }
        }, 2000)
    }
    private fun setError(textlayout: TextInputLayout, errorReason: String) {
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
        updateNavTitles()
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
    private fun updateWebcamUrl(url: String, textlayout: TextInputLayout) {
        toggleInputEdit(textlayout)
        Thread {
            val webcamURL = URL(url)
            val webcamConnection = webcamURL.openConnection() as HttpURLConnection
            webcamConnection.connectTimeout = 2000
            webcamConnection.readTimeout = 2000
            try {
                webcamConnection.connect()
                toggleInputEdit(textlayout)
                if(webcamConnection.contentType.contains("text/html")){
                    setError(textlayout, "Invalid URL!")
                    return@Thread
                }
                savePrinterData("webcamurl", url)
                setSaved(textlayout, "Saved URL!")
            } catch (e: Exception){
                setError(textlayout, "Invalid URL!")
                toggleInputEdit(textlayout)
            } finally {
                webcamConnection.disconnect()
            }
        }.start()
    }
    private fun updateNavTitles() {
        NavTitles.updateSubTitle(printerData.getString("websocketurl"))
        NavTitles.updateTitle(currentPrinter)
    }
    private fun updateWebsocket(url: String, textlayout: TextInputLayout) {
        var validated = false
        toggleInputEdit(textlayout)
        webSocketClient = object : WebSocketClient(URI(url)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                toggleInputEdit(textlayout)
                savePrinterData("websocketurl", url)
                setSaved(textlayout, "Saved URL!")
                validated = true
                close()
            }

            override fun onMessage(message: String?) {
                toggleInputEdit(textlayout)
                savePrinterData("websocketurl", url)
                setSaved(textlayout, "Saved URL!")
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