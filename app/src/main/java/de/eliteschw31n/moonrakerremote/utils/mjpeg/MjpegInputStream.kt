package de.eliteschw31n.moonrakerremote.utils.mjpeg

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.util.*


class MjpegInputStream(`in`: InputStream?) : DataInputStream(BufferedInputStream(`in`, FRAME_MAX_LENGTH)) {
    private val SOI_MARKER = byteArrayOf(0xFF.toByte(), 0xD8.toByte())
    private val EOF_MARKER = byteArrayOf(0xFF.toByte(), 0xD9.toByte())
    private val CONTENT_LENGTH = "Content-Length"
    private var mContentLength = -1
    @Throws(IOException::class)
    private fun getEndOfSeqeunce(`in`: DataInputStream, sequence: ByteArray): Int {
        var seqIndex = 0
        var c: Byte
        for (i in 0 until FRAME_MAX_LENGTH) {
            c = `in`.readUnsignedByte().toByte()
            if (c == sequence[seqIndex]) {
                seqIndex++
                if (seqIndex == sequence.size) return i + 1
            } else seqIndex = 0
        }
        return -1
    }

    @Throws(IOException::class)
    private fun getStartOfSequence(`in`: DataInputStream, sequence: ByteArray): Int {
        val end = getEndOfSeqeunce(`in`, sequence)
        return if (end < 0) -1 else end - sequence.size
    }

    @Throws(IOException::class, NumberFormatException::class)
    private fun parseContentLength(headerBytes: ByteArray): Int {
        val headerIn = ByteArrayInputStream(headerBytes)
        val props = Properties()
        props.load(headerIn)
        return props.getProperty(CONTENT_LENGTH).toInt()
    }

    @Throws(IOException::class)
    fun readMjpegFrame(): Bitmap {
        mark(FRAME_MAX_LENGTH)
        val headerLen = getStartOfSequence(this, SOI_MARKER)
        reset()
        val header = ByteArray(headerLen)
        readFully(header)
        mContentLength = try {
            parseContentLength(header)
        } catch (nfe: NumberFormatException) {
            getEndOfSeqeunce(this, EOF_MARKER)
        }
        reset()
        val frameData = ByteArray(mContentLength)
        skipBytes(headerLen)
        readFully(frameData)
        return BitmapFactory.decodeStream(ByteArrayInputStream(frameData))
    }

    companion object {
        private const val HEADER_MAX_LENGTH = 100
        private const val FRAME_MAX_LENGTH = 40000 + HEADER_MAX_LENGTH
        fun read(url: String?): MjpegInputStream? {
            val webcamURL = URL(url)
            val webcamConnection = webcamURL.openConnection() as HttpURLConnection
            var stream: MjpegInputStream? = null
            webcamConnection.connectTimeout = 2000
            webcamConnection.readTimeout = 2000
            try {
                val webThread = Thread {
                    webcamConnection.connect()
                    stream = MjpegInputStream(webcamConnection.inputStream)
                }
                webThread.start()
                webThread.join()
                return stream
            } catch (e: Exception){
                e.printStackTrace()
            }
            return stream
        }
    }
}