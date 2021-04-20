package de.eliteschw31n.moonrakerremote.ui.settings

import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import de.eliteschw31n.moonrakerremote.MainActivity
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.NotificationActionHandler
import de.eliteschw31n.moonrakerremote.utils.NotificationUtil


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_settings, container, false)

        val notifyTestButton : Button = root.findViewById(R.id.settings_notify)
        notifyTestButton.setOnClickListener {
            val intentAction = Intent(context, NotificationActionHandler::class.java)
            intentAction.putExtra("action","actionName")
            val pendingIntent = PendingIntent.getBroadcast(context,1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);
            val notifyBuilder = NotificationCompat.Builder(MainActivity.applicationContext(), "-1")
                .setSmallIcon(R.drawable.ic_app_logo)
                .setContentTitle("Title")
                .setContentText("LONG TEXT MAYBE")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_app_logo, "action1", pendingIntent)
            NotificationUtil.notify(notifyBuilder, 1)
        }

        val notifyTestButton2 : Button = root.findViewById(R.id.settings_notify2)
        notifyTestButton2.setOnClickListener {
            val intentAction = Intent(context, NotificationActionHandler::class.java)
            intentAction.putExtra("action","actionName")
            val pendingIntent = PendingIntent.getBroadcast(context,1,intentAction,PendingIntent.FLAG_UPDATE_CURRENT);
            val notifyBuilder = NotificationCompat.Builder(MainActivity.applicationContext(), "-1")
                    .setSmallIcon(R.drawable.ic_app_logo)
                    .setContentTitle("Title2")
                    .setContentText("LONG TEXT MAYBE")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .addAction(R.drawable.ic_app_logo, "action1", pendingIntent)
            NotificationUtil.notify(notifyBuilder, 1)
        }
        return root
    }
}