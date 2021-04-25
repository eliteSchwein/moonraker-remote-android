package de.eliteschw31n.moonrakerremote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import de.eliteschw31n.moonrakerremote.tasks.WebsocketTask
import de.eliteschw31n.moonrakerremote.utils.NotificationUtil

class NotificationActionHandler : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.getStringExtra("action")
        val notificationId = intent.getIntExtra("notificationID",0)
        Log.d("action", action.toString())

        if(intent.getBooleanExtra("closeNotifications", false)) {
            val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
            context.sendBroadcast(it)
        }
        if(notificationId != 0) {
            NotificationUtil.close(notificationId)
        }

        if(action.toString() == "websocketReconnect") {
            WebsocketTask.reconnect()
        }
    }
}