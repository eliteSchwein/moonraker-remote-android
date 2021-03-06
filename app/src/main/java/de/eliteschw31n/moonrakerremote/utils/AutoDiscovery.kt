package de.eliteschw31n.moonrakerremote.utils

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import de.eliteschw31n.moonrakerremote.MainActivity
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.URI
import java.util.*
import kotlin.random.Random


class AutoDiscovery {
    private lateinit var partialIP: String
    private var discoveredAddresses: JSONObject = JSONObject()
    private var subIP = 1
    private var scanning = false

    fun isScanning(): Boolean {
        return scanning
    }
    fun getAddresses(): JSONObject {
        return discoveredAddresses
    }
    fun searchAddresses(): String {
        if(scanning) {
            return "Already Scanning!"
        }
        val context = MainActivity.applicationContext().applicationContext
        val connManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val currentNetwork = connManager.activeNetworkInfo
        if(currentNetwork?.type != ConnectivityManager.TYPE_WIFI) {
            return "Not Connected to Wifi"
        }
        val ipAddress = getIPAddress(true)
        partialIP = "${ipAddress?.subSequence(0, ipAddress.lastIndexOf(".") + 1)}"
        subIP = 1
        scanning = true
        discoverAddresses(false)
        return "Scanning..."
    }

    private fun discoverAddresses(useAltPort: Boolean) {
        if(subIP == 255){
            this.scanning = false
            return
        }
        var websocketURL = "ws://$partialIP$subIP/websocket"
        if(useAltPort){
            websocketURL = "ws://$partialIP$subIP:7125/websocket"
        }
        Thread {
            if(!useAltPort) {
                scanning = true
            }
            var nextScanTriggered = false
            val webSocketClient = object : WebSocketClient(URI(websocketURL), Draft_6455(), null, 100) {
                override fun onOpen(handshakedata: ServerHandshake?) {
                    send("{\"jsonrpc\": \"2.0\",\"method\": \"printer.info\",\"id\": ${Random.nextInt(1,10000)}}")
                }

                override fun onMessage(message: String?) {
                    val jsonMessage = JSONObject(message)
                    if(jsonMessage.has("result")){
                        val jsonResult = jsonMessage.getJSONObject("result")
                        if(jsonResult.has("klipper_path")){
                            val discoveredPrinter = JSONObject()
                            discoveredPrinter.put("websocketurl", websocketURL)
                            discoveredPrinter.put("webcamurl", "http://$partialIP$subIP/webcam/?action=stream")
                            discoveredAddresses.put(partialIP + subIP, discoveredPrinter)
                            Log.d("Discovered Printer", websocketURL)
                        }
                    }
                    close()
                }

                override fun onClose(code: Int, reason: String?, remote: Boolean) {
                    if(!nextScanTriggered && !useAltPort) {
                        scanNextDiscovery()
                        nextScanTriggered = true
                    }
                }

                override fun onError(ex: Exception?) {
                    if(!useAltPort) {
                        discoverAddresses(true)
                    }
                    if(!nextScanTriggered && !useAltPort) {
                        scanNextDiscovery()
                        nextScanTriggered = true
                    }
                }
            }
            webSocketClient.connect()
        }.start()
    }
    private fun scanNextDiscovery() {
        subIP++
        discoverAddresses(false)
    }

    private fun getIPAddress(useIPv4: Boolean): String? {
        try {
            val interfaces: List<NetworkInterface> = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs: List<InetAddress> = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr: String = addr.hostAddress
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (useIPv4) {
                            if (isIPv4) return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ignored: Exception) {

        }
        return ""
    }

}