package de.eliteschw31n.moonrakerremote.ui.components

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.eliteschw31n.moonrakerremote.R

class PrinterSelect : Fragment()  {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.component_printer_select, container, false)

        return root
    }
}