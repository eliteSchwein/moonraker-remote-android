package de.eliteschw31n.moonrakerremote.utils

import android.content.Context
import org.json.JSONObject
import java.io.File

class LocalDatabase {
    private var localData: JSONObject = JSONObject()

    fun readData(context: Context) {
        if(!isFilePresent(context, "storage.json")) {
            create(context, "storage.json","{\"printers\": {}")
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