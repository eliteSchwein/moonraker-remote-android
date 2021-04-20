package de.eliteschw31n.moonrakerremote.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import de.eliteschw31n.moonrakerremote.MainActivity
import org.json.JSONObject
import kotlin.random.Random

class NotificationUtil {
    companion object {
        fun registerChannels(){
            val channels = JSONObject(FileUtils.loadJSONFromAsset("notificationChannels.json"))
            val keys = channels.keys()
            while (keys.hasNext()){
                val key = keys.next()
                val data = channels.getJSONObject(key)
                registerChannel(key, data.getString("name"), data.getString("description"))
            }
        }
        fun notify(notifyBuilder: NotificationCompat.Builder, id: Int) {
            with(NotificationManagerCompat.from(MainActivity.applicationContext())) {
                notify( id, notifyBuilder.build())
            }
        }
        fun close(id: Int) {
            with(NotificationManagerCompat.from(MainActivity.applicationContext())) {
                cancel(id)
            }
        }
        private fun registerChannel(id: String, name: String, description: String) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val mChannel = NotificationChannel(id, name, importance)
                mChannel.description = description
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                val notificationManager = MainActivity.applicationContext().getSystemService(
                    NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(mChannel)
            }
        }
    }
}