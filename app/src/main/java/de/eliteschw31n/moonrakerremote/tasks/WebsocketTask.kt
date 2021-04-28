package de.eliteschw31n.moonrakerremote.tasks

import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import de.eliteschw31n.moonrakerremote.MainActivity
import de.eliteschw31n.moonrakerremote.NotificationActionHandler
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.NotificationUtil
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

        fun reconnect() {
            websocket.reconnect()
        }

        fun disconnect() {
            Thread.currentThread().interrupt()
        }

        fun isConnected(): Boolean {
            if(!this::websocket.isInitialized) {
                return false
            }
            return websocket.isOpen
        }

        fun connect(URL: String) {
            if(isConnected()) {
                disconnect()
            }
            backgroundThread = Thread {
                if(Thread.interrupted()) {
                    if(isConnected()) {
                        websocket.close()
                        return@Thread
                    }
                }
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
                        val intentAction = Intent(MainActivity.applicationContext(), NotificationActionHandler::class.java)
                        intentAction.putExtra("action","websocketReconnect")
                        intentAction.putExtra("notificationID", 1)
                        intentAction.putExtra("closeNotifications", true)
                        val pendingIntent = PendingIntent.getBroadcast(MainActivity.applicationContext(),1,intentAction, PendingIntent.FLAG_UPDATE_CURRENT);
                        val notifyBuilder = NotificationCompat.Builder(MainActivity.applicationContext(), "1")
                                .setSmallIcon(R.drawable.ic_disconnected)
                                .setContentTitle("Connection Lost")
                                .setContentText("Reason: $reason")
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                .addAction(R.drawable.ic_app_logo, "Reconnect", pendingIntent)
                        NotificationUtil.notify(notifyBuilder, 1)
                    }

                    override fun onError(ex: Exception?) {

                    }
                }
                websocket.connect()
            }
            backgroundThread.start()
        }
    }
}
