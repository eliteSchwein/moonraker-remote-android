package de.eliteschw31n.moonrakerremote.utils

import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import de.eliteschw31n.moonrakerremote.MainActivity
import de.eliteschw31n.moonrakerremote.R

class NavTitles {
    companion object {
        private lateinit var navView: NavigationView

        fun setNavView(navigationView: NavigationView){
            navView = navigationView
        }

        fun updateTitles(){
            val currentPrinter = LocalDatabase.getData().getString("currentPrinter")
            val printerData = LocalDatabase.getPrinterData(currentPrinter)

            updateSubTitle(printerData.getString("websocketurl"))
            updateTitle(currentPrinter)
        }

        fun updateTitle(title: String) {
            MainActivity.runUiUpdate(Runnable {
                val header = navView.getHeaderView(0)
                val textTitle: TextView = header.findViewById(R.id.nav_header_title)
                textTitle.text = title
            })
        }

        fun updateSubTitle(subTitle: String) {
            MainActivity.runUiUpdate(Runnable {
                val header = navView.getHeaderView(0)
                val textSubTitle: TextView = header.findViewById(R.id.nav_header_subtitle)
                textSubTitle.text = subTitle
            })
        }
    }
}