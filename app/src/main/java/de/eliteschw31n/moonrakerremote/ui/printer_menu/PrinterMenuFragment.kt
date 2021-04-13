package de.eliteschw31n.moonrakerremote.ui.printer_menu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.navigation.NavigationView
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase
import org.json.JSONObject

class PrinterMenuFragment : Fragment() {

    private lateinit var printerMenuViewModel: PrinterMenuViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        printerMenuViewModel =
                ViewModelProvider(this).get(PrinterMenuViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_printer_menu, container, false)
        val printerSelection: LinearLayout = root.findViewById(R.id.printer_menu_selection)
        val printers = LocalDatabase.getData().getJSONObject("printers")
        val currentPrinter = LocalDatabase.getData().getString("currentPrinter")
        for (printer in printers.keys()){
            val printerButton = Button(context)
            printerButton.layoutParams =
                RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT)
            printerButton.text = printer
            printerButton.tag = "printer_select_$printer"
            if(currentPrinter == printer){
                printerButton.isEnabled = false
            }
            printerSelection.addView(printerButton)
        }
        return root
    }
}