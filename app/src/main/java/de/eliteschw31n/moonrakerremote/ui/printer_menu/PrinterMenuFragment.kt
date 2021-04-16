package de.eliteschw31n.moonrakerremote.ui.printer_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.eliteschw31n.moonrakerremote.MainActivity
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase
import de.eliteschw31n.moonrakerremote.utils.NavTitles
import org.json.JSONObject
import java.util.*
import kotlin.random.Random


class PrinterMenuFragment : Fragment() {
    private lateinit var printerProfiles: JSONObject
    private lateinit var currentPrinter: String
    private lateinit var printerSelection: ConstraintLayout
    private var lastButton: Int = 0

    private lateinit var printerMenuViewModel: PrinterMenuViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        printerMenuViewModel =
                ViewModelProvider(this).get(PrinterMenuViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_printer_menu, container, false)

        printerProfiles = LocalDatabase.getData().getJSONObject("printers")
        currentPrinter = LocalDatabase.getData().getString("currentPrinter")

        printerSelection = root.findViewById(R.id.printer_menu_profile_layout)
        loadPrinterProfileButtons()

        val addButton: FloatingActionButton = root.findViewById(R.id.printer_menu_add)
        addButton.setOnClickListener {
            val defaultData = JSONObject()
            defaultData.put("websocketurl", "ws://mainsailos.local/websocket")
            defaultData.put("webcamurl", "http://mainsailos.local/webcam/?action=stream")
            val profileName = generateName()
            LocalDatabase.updatePrinterData(profileName, defaultData)
            addPrinterProfileButton(profileName)
        }
        return root
    }
    private fun handleProfileButton(printerButton: Button) {
        printerButton.setOnClickListener {
            val database = LocalDatabase.getData()
            val tag = printerButton.tag.toString()
            database.put("currentPrinter", tag.replace("printer_select_", ""))
            val oldSelectedProfile: Button = printerSelection.findViewWithTag("printer_select_$currentPrinter")
            oldSelectedProfile.isEnabled = true
            currentPrinter = tag.replace("printer_select_", "")
            LocalDatabase.writeData(database)
            NavTitles.updateTitles()
            printerButton.isEnabled = false
        }
    }
    private fun loadPrinterProfileButtons() {
        lastButton = 0
        MainActivity.runUiUpdate(Runnable {
            val printers = LocalDatabase.getData().getJSONObject("printers")
            for (printer in printers.keys()){
                addPrinterProfileButton(printer)
            }
        })
    }
    private fun addPrinterProfileButton(name: String) {
        val printerButton = Button(context)
        printerButton.text = name
        printerButton.id = View.generateViewId()
        printerButton.tag = "printer_select_$name"
        val layoutParams =
                LinearLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT)
        printerButton.layoutParams = layoutParams
        if(currentPrinter == name){
            printerButton.isEnabled = false
        }
        printerSelection.addView(printerButton)
        val constrainSet = ConstraintSet()
        constrainSet.clone(printerSelection)
        if(lastButton != 0) {
            constrainSet.connect(printerButton.id, ConstraintSet.TOP, lastButton, ConstraintSet.BOTTOM, 16)
        }
        lastButton = printerButton.id
        constrainSet.applyTo(printerSelection)
        handleProfileButton(printerButton)
    }
    private fun generateName(): String {
        val generatedName = "profile" + Random.nextInt(0, 1000)
        printerProfiles.keys().forEach {
            if(generatedName == it){
                return generateName()
            }
        }
        return generatedName
    }
}