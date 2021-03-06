package de.eliteschw31n.moonrakerremote.ui.printer_settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputLayout
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.InputUtil
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase
import de.eliteschw31n.moonrakerremote.utils.NavTitles
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.*
import java.util.*

class SettingsFragment : Fragment() {
    private lateinit var webSocketClient: WebSocketClient
    private lateinit var printerData: JSONObject
    private lateinit var printerProfiles: JSONObject
    private lateinit var currentPrinter: String
    private lateinit var fragmentLayout: ConstraintLayout
    private lateinit var websocketTextLayout: TextInputLayout
    private lateinit var webcamTextLayout: TextInputLayout
    private val discoveryHandler = DiscoveryHandler()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_printer_settings, container, false)

        discoveryHandler.setView(root)

        currentPrinter = LocalDatabase.getData().getString("editPrinter")
        printerData = LocalDatabase.getPrinterData(currentPrinter)
        printerProfiles = LocalDatabase.getData().getJSONObject("printers")

        fragmentLayout = root.findViewById(R.id.printer_settings_activity)

        val deleteButton : Button = root.findViewById(R.id.printer_settings_delete_profile)

        if(printerProfiles.length() > 1) {
            handleDeleteButton(deleteButton)
        } else {
            fragmentLayout.removeView(deleteButton)
        }

        val nameTextLayout: TextInputLayout = root.findViewById(R.id.input_printer_settings_name)
        val nameTextEdit = nameTextLayout.editText
        nameTextEdit?.setText(currentPrinter)
        nameTextLayout.setEndIconOnClickListener {
            val profileName = nameTextEdit?.text.toString()
            if(profileName.isNullOrBlank() || profileName.isNullOrEmpty()){
                InputUtil.setError(nameTextLayout, "Empty!")
                return@setEndIconOnClickListener
            }
            if(profileName.length > 64) {
                InputUtil.setError(nameTextLayout, "Too long (max: 64)!")
                return@setEndIconOnClickListener
            }
            printerProfiles.keys().forEach {
                val profile = it
                Log.d("profile", profile)
                if(profileName == profile){
                    InputUtil.setError(nameTextLayout, "Name already in use!")
                    return@setEndIconOnClickListener
                }
            }
            replaceName(profileName)
            InputUtil.setSuccess(nameTextLayout, "Saved new Profile Name!")
        }

        websocketTextLayout = root.findViewById(R.id.input_printer_settings_websocket)
        val websocketTextEdit = websocketTextLayout.editText
        websocketTextEdit?.setText(printerData.getString("websocketurl"))
        websocketTextLayout.setEndIconOnClickListener {
            val websocketURL = websocketTextEdit?.text.toString()
            updateWebsocket(websocketURL, websocketTextLayout)
        }

        webcamTextLayout = root.findViewById(R.id.input_printer_settings_stream_url)
        val webcamTextEdit = webcamTextLayout.editText

        webcamTextEdit?.setText(printerData.getString("webcamurl"))
        webcamTextLayout.setEndIconOnClickListener {
            val webcamURL = webcamTextEdit?.text.toString()
            updateWebcamUrl(webcamURL, webcamTextLayout)
        }
        discoveryHandler.init()
        return root
    }

    private fun handleDeleteButton(deleteButton: Button) {
        deleteButton.setOnClickListener {
            printerProfiles.remove(currentPrinter)
            val database = LocalDatabase.getData()
            database.put("printers", printerProfiles)
            if(LocalDatabase.getData().getString("currentPrinter") == currentPrinter) {
                database.put("currentPrinter", printerProfiles.keys().next())
            }
            LocalDatabase.writeData(database)
            NavTitles.updateTitles()
            findNavController().navigate(R.id.nav_printer_menu)
        }
    }
    private fun replaceName(name:String) {
        printerProfiles.remove(currentPrinter)
        printerProfiles.put(name, printerData)
        val database = LocalDatabase.getData()
        database.put("printers", printerProfiles)
        if(database.getString("currentPrinter") == currentPrinter){
            database.put("currentPrinter", name)
        }
        LocalDatabase.writeData(database)
        currentPrinter = name
        NavTitles.updateTitles()
    }
    private fun savePrinterData(key: String, data: Any) {
        printerData = LocalDatabase.getPrinterData(currentPrinter)

        printerData.put(key, data)
        LocalDatabase.updatePrinterData(currentPrinter, printerData)
        NavTitles.updateTitles()
    }
    private fun updateWebcamUrl(url: String, textlayout: TextInputLayout) {
        InputUtil.toggleInputEdit(textlayout)
        Thread {
            val webcamURL = URL(url)
            val webcamConnection = webcamURL.openConnection() as HttpURLConnection
            webcamConnection.connectTimeout = 2000
            webcamConnection.readTimeout = 2000
            try {
                webcamConnection.connect()
                InputUtil.toggleInputEdit(textlayout)
                if(webcamConnection.contentType.contains("text/html")){
                    InputUtil.setError(textlayout, "Invalid URL!")
                    return@Thread
                }
                savePrinterData("webcamurl", url)
                InputUtil.setSuccess(textlayout, "Saved URL!")
            } catch (e: Exception){
                InputUtil.setError(textlayout, "Invalid URL!")
                InputUtil.toggleInputEdit(textlayout)
            } finally {
                webcamConnection.disconnect()
            }
        }.start()
    }
    private fun updateWebsocket(url: String, textlayout: TextInputLayout) {
        var validated = false
        InputUtil.toggleInputEdit(textlayout)
        webSocketClient = object : WebSocketClient(URI(url)) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                InputUtil.toggleInputEdit(textlayout)
                savePrinterData("websocketurl", url)
                InputUtil.setSuccess(textlayout, "Saved URL!")
                validated = true
                close()
            }

            override fun onMessage(message: String?) {
                InputUtil.toggleInputEdit(textlayout)
                savePrinterData("websocketurl", url)
                InputUtil.setSuccess(textlayout, "Saved URL!")
                validated = true
                close()
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                if(!validated){
                    InputUtil.toggleInputEdit(textlayout)
                    InputUtil.setError(textlayout, "Invalid URL!")
                }
            }

            override fun onError(ex: Exception?) {
                InputUtil.toggleInputEdit(textlayout)
                InputUtil.setError(textlayout, "Invalid URL!")
                validated = true
            }
        }
        webSocketClient.connect()
    }
}