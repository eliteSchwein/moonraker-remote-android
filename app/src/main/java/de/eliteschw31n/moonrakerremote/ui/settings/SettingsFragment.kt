package de.eliteschw31n.moonrakerremote.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.eliteschw31n.moonrakerremote.R

class SettingsFragment : Fragment() {

    private lateinit var settingsMenuViewModel: SettingsViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        settingsMenuViewModel =
                ViewModelProvider(this).get(SettingsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val printerWebsocket: TextView = root.findViewById(R.id.input_settings_connection_websocket)
        settingsMenuViewModel.printerWebsocket.observe(viewLifecycleOwner, Observer {
            printerWebsocket.text = it
        })
        val printerStreamURL: TextView = root.findViewById(R.id.input_settings_connection_stream_url)
        settingsMenuViewModel.printerStreamURL.observe(viewLifecycleOwner, Observer {
            printerStreamURL.text = it
        })
        return root
    }
}