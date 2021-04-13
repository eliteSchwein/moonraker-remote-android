package de.eliteschw31n.moonrakerremote.ui

import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.navigation.NavigationView
import de.eliteschw31n.moonrakerremote.MainActivity
import de.eliteschw31n.moonrakerremote.R

class NavTitles {
    fun updateTitle(title: String, navHeader: NavigationView){
        val header = navHeader.getHeaderView(0)
        val textTitle: TextView = header.findViewById(R.id.nav_header_title)
        textTitle.text = title
    }
    fun updateSubTitle(subTitle: String, navHeader: NavigationView){
        val header = navHeader.getHeaderView(0)
        val textSubTitle: TextView = header.findViewById(R.id.nav_header_subtitle)
        textSubTitle.text = subTitle
    }
}