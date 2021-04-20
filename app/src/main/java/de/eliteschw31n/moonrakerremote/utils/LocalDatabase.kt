package de.eliteschw31n.moonrakerremote.utils

import android.content.Context
import de.eliteschw31n.moonrakerremote.MainActivity
import org.json.JSONObject
import java.io.File

class LocalDatabase {
    private val context = MainActivity.applicationContext()

    private var localData: JSONObject = JSONObject("{\"notLoaded\":true}")

    companion object{
        val localDatabase = LocalDatabase()
        fun readData(){
            localDatabase.readData()
        }
        fun getData() :JSONObject {
            return localDatabase.getData()
        }
        fun getPrinterData(printer: String) :JSONObject {
            return localDatabase.getData()
                .getJSONObject("printers")
                .getJSONObject(printer)
        }
        fun updatePrinterData(printer: String, printerData: JSONObject) {
            val data  = localDatabase.getData()
            val printersData = data
                .getJSONObject("printers")
                .put(printer, printerData)
            data.put("printers", printersData)
            localDatabase.writeData(data)
        }
        fun writeData(data: JSONObject) {
            localDatabase.writeData(data)
        }
    }

    fun readData() {
        if(!isFilePresent("storage.json")) {
            print("Generate File")
            create("storage.json","{\"currentPrinter\":\"default\",\"printers\": { \"default\": { \"websocketurl\": \"ws://mainsailos.local/websocket\", \"webcamurl\": \"http://mainsailos.local/webcam/?action=stream\"}} }")
        }
        context.openFileInput("storage.json").use { stream ->
            val data = stream.bufferedReader().use {
                it.readText()
            }
            localData = JSONObject(data)
        }
    }

    fun getData(): JSONObject {
        return localData
    }

    fun writeData(data: JSONObject) {
        localData = data
        context.openFileOutput("storage.json", Context.MODE_PRIVATE).use { output ->
            output.write(data.toString().toByteArray())
        }
    }

    private fun create(fileName: String, fileData: String){
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
            output.write(fileData.toByteArray())
        }
    }

    private fun isFilePresent(fileName: String): Boolean {
        return File(context.filesDir.absolutePath + "/" + fileName).exists()
    }
}