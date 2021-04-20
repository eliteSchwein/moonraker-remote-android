package de.eliteschw31n.moonrakerremote.utils

import de.eliteschw31n.moonrakerremote.MainActivity
import java.io.IOException
import java.io.InputStream

class FileUtils {
    companion object {
        fun loadJSONFromAsset(file : String): String? {
            var json: String?
            json = try {
                val `is`: InputStream = MainActivity.applicationContext().assets.open(file)
                val size: Int = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                `is`.close()
                String(buffer, charset("UTF-8"))
            } catch (ex: IOException) {
                ex.printStackTrace()
                return null
            }
            return json
        }
    }
}