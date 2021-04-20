package de.eliteschw31n.moonrakerremote.ui.printer_settings

import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.textfield.TextInputLayout
import de.eliteschw31n.moonrakerremote.MainActivity
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.AutoDiscovery
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase
import de.eliteschw31n.moonrakerremote.utils.NavTitles
import org.json.JSONObject
import java.util.*

class DiscoveryHandler {
    private lateinit var fragmentView: View
    private lateinit var discoveryLoading: ProgressBar
    private lateinit var discoveryErrorText: TextView
    private lateinit var discoverySaveText: TextView
    private lateinit var discoveryNextButton: Button
    private lateinit var discoveryDiscoverButton: Button
    private lateinit var discoveredAddresses: JSONObject
    private lateinit var addressesKeys: Iterator<String>

    private val autoDiscovery = AutoDiscovery()

    fun setView(view: View) {
       fragmentView = view
    }

    fun init() {
        discoveryLoading = fragmentView.findViewById(R.id.printer_settings_discovery_loading)
        discoveryNextButton = fragmentView.findViewById(R.id.printer_settings_discovery_next)
        discoveryErrorText = fragmentView.findViewById(R.id.printer_settings_discovery_error_text)
        discoverySaveText = fragmentView.findViewById(R.id.printer_settings_discovery_save_text)
        discoveryDiscoverButton= fragmentView.findViewById(R.id.printer_settings_auto_discover)
        handleDiscoveryButton()
        handleDiscoveryNextButton()
    }

    private fun retrieveNextDiscovery() {
        if(!addressesKeys.hasNext()) {
            addressesKeys = discoveredAddresses.keys()
        }

        val nextIndex = addressesKeys.next()
        val nextAddresses = discoveredAddresses.getJSONObject(nextIndex)

        val currentPrinter = LocalDatabase.getData().getString("currentPrinter")

        val websocketTextLayout : TextInputLayout = fragmentView.findViewById(R.id.input_printer_settings_websocket)
        val websocketTextEdit = websocketTextLayout.editText

        val webcamTextLayout : TextInputLayout  = fragmentView.findViewById(R.id.input_printer_settings_stream_url)
        val webcamTextEdit = webcamTextLayout.editText

        MainActivity.runUiUpdate(Runnable {
            websocketTextEdit?.setText(nextAddresses.getString("websocketurl"))
            webcamTextEdit?.setText(nextAddresses.getString("webcamurl"))
        })

        LocalDatabase.updatePrinterData(currentPrinter, nextAddresses)

        NavTitles.updateTitles()

        setDiscoverySaved()
    }

    private fun handleDiscoveryNextButton() {
        discoveryNextButton.setOnClickListener {
            retrieveNextDiscovery()
        }
    }

    private fun handleDiscoveryButton() {
        discoveryDiscoverButton.setOnClickListener {
            MainActivity.runUiUpdate(Runnable {
                discoveryDiscoverButton.isEnabled = false
                discoveryLoading.visibility = View.VISIBLE
                discoveryNextButton.visibility = View.INVISIBLE
            })
            val validConnection = autoDiscovery.searchAddresses()
            if(validConnection != "Scanning...") {
                MainActivity.runUiUpdate(Runnable {
                    setDiscoveryError(validConnection)
                    discoveryDiscoverButton.isEnabled = true
                    discoveryLoading.visibility = View.INVISIBLE
                })
                return@setOnClickListener
            }
            Thread {
                while (autoDiscovery.isScanning()){ }
                MainActivity.runUiUpdate(Runnable {
                    discoveryDiscoverButton.isEnabled = true
                    discoveryLoading.visibility = View.INVISIBLE
                    discoveredAddresses = autoDiscovery.getAddresses()
                    addressesKeys = discoveredAddresses.keys()
                    if(discoveredAddresses.length() > 1) {
                        discoveryNextButton.visibility = View.VISIBLE
                    }
                    if(discoveredAddresses.length() != 0) {
                        retrieveNextDiscovery()
                    }
                })
            }.start()
        }
    }
    private fun setDiscoveryError(errorReason: String) {
        MainActivity.runUiUpdate(Runnable {
            discoveryDiscoverButton.error = errorReason
            discoveryErrorText.text = errorReason
            discoveryErrorText.visibility = View.VISIBLE
        })
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.runUiUpdate(Runnable {
                    discoveryDiscoverButton.error = null
                    discoveryErrorText.visibility = View.INVISIBLE
                })
            }
        }, 2000)
    }
    private fun setDiscoverySaved() {
        MainActivity.runUiUpdate(Runnable {
            discoverySaveText.visibility = View.VISIBLE
        })
        Timer().schedule(object : TimerTask() {
            override fun run() {
                MainActivity.runUiUpdate(Runnable {
                    discoverySaveText.visibility = View.INVISIBLE
                })
            }
        }, 2000)
    }
}