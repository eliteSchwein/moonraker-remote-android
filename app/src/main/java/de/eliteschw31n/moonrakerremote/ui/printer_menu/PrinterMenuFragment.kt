package de.eliteschw31n.moonrakerremote.ui.printer_menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import de.eliteschw31n.moonrakerremote.MainActivity
import de.eliteschw31n.moonrakerremote.R
import de.eliteschw31n.moonrakerremote.ui.components.PrinterSelect
import de.eliteschw31n.moonrakerremote.utils.LocalDatabase
import de.eliteschw31n.moonrakerremote.utils.NavTitles
import org.json.JSONObject
import kotlin.random.Random


class PrinterMenuFragment : Fragment() {
    private lateinit var printerProfiles: JSONObject
    private lateinit var currentPrinter: String
    private lateinit var printerSelection: ConstraintLayout
    private lateinit var profilesLayout: FrameLayout
    private var lastButton: Int = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_printer_menu, container, false)

        printerProfiles = LocalDatabase.getData().getJSONObject("printers")
        currentPrinter = LocalDatabase.getData().getString("currentPrinter")

        printerSelection = root.findViewById(R.id.printer_menu_profile_layout)

        profilesLayout = root.findViewById(R.id.printer_menu_profiles_layout)
        //loadPrinterProfileButtons()
        loadPrinterProfiles()

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
    private fun handleProfileEditButton(printerEditButton: ImageButton) {
        printerEditButton.setOnClickListener {
            val database = LocalDatabase.getData()
            val tag = printerEditButton.tag.toString()
            database.put("currentPrinter", tag.replace("printer_edit_", ""))
            findNavController().navigate(R.id.nav_printer_settings)
        }
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
    private fun loadPrinterProfiles() {
        lastButton = 0
        MainActivity.runUiUpdate(Runnable {
            val printers = LocalDatabase.getData().getJSONObject("printers")
            for (printer in printers.keys()){
                addPrinterProfile(printer)
            }
        })
    }
    private fun addPrinterProfile(name: String) {
        var printerProfile = PrinterSelect()
        val profileData = LocalDatabase.getPrinterData(name)
        var fragmentTransaction = MainActivity.supportFragmentManager().beginTransaction()
        fragmentTransaction.add(R.id.printer_menu_profiles_layout, printerProfile)
        fragmentTransaction.commit()
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
        constrainSet.connect(printerButton.id, ConstraintSet.LEFT, R.id.printer_settings_activity, ConstraintSet.LEFT, 32)
        constrainSet.connect(printerButton.id, ConstraintSet.RIGHT, R.id.printer_settings_activity, ConstraintSet.RIGHT, 32)
        lastButton = printerButton.id
        addPrinterProfileEditButton(name)
        constrainSet.applyTo(printerSelection)
        handleProfileButton(printerButton)
    }
    private fun addPrinterProfileEditButton(name: String) {
        val printerEditButton = ImageButton(context)
        printerEditButton.setImageResource(R.drawable.ic_settings_edit)
        printerEditButton.setBackgroundResource(R.color.transparent)
                printerEditButton.id = View.generateViewId()
        printerEditButton.tag = "printer_edit_$name"
        val layoutParams =
                LinearLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.MATCH_PARENT)
        printerEditButton.layoutParams = layoutParams
        printerSelection.addView(printerEditButton)
        val constrainSet = ConstraintSet()
        constrainSet.clone(printerSelection)
        constrainSet.connect(printerEditButton.id, ConstraintSet.RIGHT, lastButton, ConstraintSet.RIGHT, 32)
        constrainSet.connect(printerEditButton.id, ConstraintSet.TOP, lastButton, ConstraintSet.TOP, 0)
        constrainSet.connect(printerEditButton.id, ConstraintSet.BOTTOM, lastButton, ConstraintSet.BOTTOM, 0)
        constrainSet.applyTo(printerSelection)
        handleProfileEditButton(printerEditButton)
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