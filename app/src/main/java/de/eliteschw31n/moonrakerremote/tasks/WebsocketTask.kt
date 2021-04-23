package de.eliteschw31n.moonrakerremote.tasks

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.drafts.Draft_6455
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONObject
import java.net.URI
import kotlin.random.Random

class WebsocketTask {
    companion object {
        private lateinit var backgroundThread: Thread
        private lateinit var websocket: WebSocketClient
        private var validServer = false

        fun disconnect() {
            Thread.currentThread().stop()
        }

        fun connect(URL: String) {
            disconnect()
            backgroundThread = Thread {
                websocket = object : WebSocketClient(URI(URL), Draft_6455(), null, 100) {
                    override fun onOpen(handshakedata: ServerHandshake?) {
                        send("{\"jsonrpc\": \"2.0\",\"method\": \"printer.info\",\"id\": ${Random.nextInt(1,10000)}}")
                    }

                    override fun onMessage(message: String?) {
                        val jsonMessage = JSONObject(message)
                        if(jsonMessage.has("result")){
                            validServer = true
                        }
                        if(!validServer) {
                            close()
                        }
                    }

                    override fun onClose(code: Int, reason: String?, remote: Boolean) {

                    }

                    override fun onError(ex: Exception?) {

                    }
                }
                websocket.connect()
            }
        }
    }
}