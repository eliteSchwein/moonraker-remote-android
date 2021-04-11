package de.eliteschw31n.moonrakerremote.ui.printer_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import de.eliteschw31n.moonrakerremote.R

class PrinterMenuFragment : Fragment() {

    private lateinit var printerMenuViewModel: PrinterMenuViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        printerMenuViewModel =
                ViewModelProvider(this).get(PrinterMenuViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_settings, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        printerMenuViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}