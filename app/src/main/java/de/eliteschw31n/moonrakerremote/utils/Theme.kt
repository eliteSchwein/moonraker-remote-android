package de.eliteschw31n.moonrakerremote.utils

import android.content.res.Configuration
import de.eliteschw31n.moonrakerremote.MainActivity

class Theme {
    companion object {
        fun isDarkMode(): Boolean {
            when (MainActivity.applicationContext().resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                Configuration.UI_MODE_NIGHT_YES -> { return true }
                Configuration.UI_MODE_NIGHT_NO -> { return false }
                Configuration.UI_MODE_NIGHT_UNDEFINED -> { return false }
            }
            return false
        }
    }
}