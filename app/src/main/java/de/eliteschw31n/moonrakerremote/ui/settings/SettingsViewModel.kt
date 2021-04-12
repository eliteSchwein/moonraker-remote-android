package de.eliteschw31n.moonrakerremote.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsViewModel : ViewModel() {

    private val _printerWebsocket = MutableLiveData<String>().apply {
        value = "http://mainsail.local/websocket"
    }

    private val _printerStreamURL = MutableLiveData<String>().apply {
        value = "http://mainsail.local/webcam/?action=stream"
    }
    val printerWebsocket: LiveData<String> = _printerWebsocket
    val printerStreamURL: LiveData<String> = _printerStreamURL
}