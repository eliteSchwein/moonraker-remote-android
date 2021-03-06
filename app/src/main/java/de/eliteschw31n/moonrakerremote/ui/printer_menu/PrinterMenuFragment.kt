package de.eliteschw31n.moonrakerremote.ui.printer_menu

import android.os.Bundle
import android.util.Log
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
    private lateinit var profilesLayout: ConstraintLayout
    private lateinit var root: View
    private var profileNames: ArrayList<String> = ArrayList()
    private var lastProfile: Int = 0

    override fun onDestroy() {
        super.onDestroy()
        clearPrinterProfiles()
    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_printer_menu, container, false)

        this.root = root

        printerProfiles = LocalDatabase.getData().getJSONObject("printers")
        currentPrinter = LocalDatabase.getData().getString("currentPrinter")

        profilesLayout = root.findViewById(R.id.printer_menu_profiles_layout)
        loadPrinterProfiles()

        val addButton: FloatingActionButton = root.findViewById(R.id.printer_menu_add)
        addButton.setOnClickListener {
            val defaultData = JSONObject()
            defaultData.put("websocketurl", "ws://mainsailos.local/websocket")
            defaultData.put("webcamurl", "http://mainsailos.local/webcam/?action=stream")
            val profileName = generateName()
            LocalDatabase.updatePrinterData(profileName, defaultData)
            LocalDatabase.setCurrentEditPrinter(profileName)
            findNavController().navigate(R.id.nav_printer_settings)
        }
        return root
    }
    private fun clearPrinterProfiles() {
        val fragmentManager =  MainActivity.supportFragmentManager()
        val fragmentTransaction = MainActivity.supportFragmentManager().beginTransaction()
        Thread {
            profileNames.forEach {
                fragmentManager.findFragmentByTag(it)?.let { it1 -> fragmentTransaction.remove(it1) }
            }
            fragmentTransaction.commit()
            profileNames.clear()
        }.start()
    }
    private fun loadPrinterProfiles() {
        lastProfile = 0
        clearPrinterProfiles()
        Thread {
            Thread.sleep(250)
            for (printer in printerProfiles.keys()) {
                addPrinterProfile(printer)
                Thread.sleep(100)
            }
        }.start()
    }
    private fun addPrinterProfile(name: String) {
        val printerProfile = PrinterSelect()
        val subLayout = FrameLayout(MainActivity.applicationContext())
        val layoutParams =
                LinearLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        600)
        val constraintSet = ConstraintSet()
        subLayout.id = View.generateViewId()
        subLayout.layoutParams = layoutParams
        MainActivity.runUiUpdate(Runnable {
            profilesLayout.addView(subLayout)
            constraintSet.clone(profilesLayout)
            if(lastProfile != 0){
                constraintSet.connect(subLayout.id, ConstraintSet.TOP, lastProfile, ConstraintSet.BOTTOM, 64)
            }
            constraintSet.applyTo(profilesLayout)
            lastProfile = subLayout.id
        })
        val fragmentTransaction = MainActivity.supportFragmentManager().beginTransaction()
        fragmentTransaction.add(subLayout.id, printerProfile, "printer_profile_$name")
        fragmentTransaction.commit()
        profileNames.add("printer_profile_$name")
        while (printerProfile.view == null) {
            Thread.sleep(100)
        }
        val profileView = printerProfile.view
        val profileTitle: TextView = profileView!!.findViewById(R.id.component_printer_select_title)
        MainActivity.runUiUpdate(Runnable {
            profileTitle.text = name
        })
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