package de.eliteschw31n.moonrakerremote.ui.printer_menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PrinterMenuViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is not slideshow Fragment"
    }
    val text: LiveData<String> = _text
}