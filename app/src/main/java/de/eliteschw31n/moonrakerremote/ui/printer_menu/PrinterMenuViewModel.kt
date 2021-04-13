package de.eliteschw31n.moonrakerremote.ui.printer_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase

class PrinterMenuViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = LocalDatabase.getData().toString()
    }
    val text: LiveData<String> = _text
}