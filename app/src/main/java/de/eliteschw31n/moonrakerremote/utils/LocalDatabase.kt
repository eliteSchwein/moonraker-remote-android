package de.eliteschw31n.moonrakerremote.utils

import android.content.Context
import org.json.JSONObject
import java.io.File

class LocalDatabase {

    private var localData: JSONObject = JSONObject("{\"notLoaded\":true}")

    companion object{
        val localDatabase = LocalDatabase()
        fun readData(context: Context){
            localDatabase.readData(context)
        }
        fun getData() :JSONObject {
            return localDatabase.getData()
        }
        fun writeData(context: Context, data: JSONObject) {
            localDatabase.writeData(context, data)
        }
    }

    fun readData(context: Context) {
        if(!isFilePresent(context, "storage.json")) {
            print("Generate File")
            create(context, "storage.json","{\"currentPrinter\":\"default\",\"printers\": { \"default\": { \"websocketurl\": \"ws://mainsail.local/websocket\", \"webcamurl\": \"http://mainsail.local/webcam/?action=stream\"}} }")
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

    fun writeData(context: Context, data: JSONObject) {
        localData = data
        context.openFileOutput("storage.json", Context.MODE_PRIVATE).use { output ->
            output.write(localData.toString().toByteArray())
        }
        readData(context)
    }

    fun create(context: Context, fileName: String, fileData: String){
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
            output.write(fileData.toByteArray())
        }
    }

    private fun isFilePresent(context: Context, fileName: String): Boolean {
        return File(context.filesDir.absolutePath + "/" + fileName).exists()
    }
}