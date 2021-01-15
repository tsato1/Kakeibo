package com.kakeibo.util

import android.content.Context
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*

object UtilFiles {

    private val TAG = UtilFiles::class.java.simpleName

    fun fileExists(context: Context, filename: String?): Boolean {
        val file = context.getFileStreamPath(filename)
        return !(file == null || !file.exists())
    }

    fun getFileValue(fileName: String?, context: Context): String? {
        return try {
            val stringBuilder = StringBuilder()
            var inputLine: String?
            /*
                * We have to use the openFileInput()-method the ActivityContext
                * provides. Again for security reasons with openFileInput(...)
                */
            val fIn = context.openFileInput(fileName)
            val isr = InputStreamReader(fIn)
            val inBuff = BufferedReader(isr)
            while (inBuff.readLine().also { inputLine = it } != null) {
                stringBuilder.append(inputLine)
                stringBuilder.append("\n")
            }
            inBuff.close()
            stringBuilder.toString()
        } catch (e: IOException) {
            null
        }
    }

    fun getFileValues(fileName: String?, context: Context): ArrayList<String>? {
        return try {
            val sb = StringBuilder()
            var inputLine: String?
            val fIn = context.openFileInput(fileName)
            val isr = InputStreamReader(fIn)
            val inBuff = BufferedReader(isr)
            while (inBuff.readLine().also { inputLine = it } != null) {
                sb.append(inputLine)
                sb.append("\n")
            }
            inBuff.close()
            ArrayList(Arrays.asList(*sb.toString().split("\t").toTypedArray()))
        } catch (e: IOException) {
            null
        }
    }

    fun appendFileValue(fileName: String, value: String, context: Context): Boolean {
        return writeToFile(fileName, value, context, Context.MODE_APPEND)
    }

    fun setFileValue(fileName: String, value: String, context: Context): Boolean {
        return writeToFile(fileName, value, context, Context.MODE_WORLD_READABLE)
    }

    fun writeToFile(fileName: String, value: String, context: Context,
                    writeOrAppendMode: Int): Boolean {
        // just make sure it's one of the modes we support
        // Context.MODE_PRIVATE: erase existing file
        // Context.MODE_APPEND: appends to an existing file
        if (writeOrAppendMode != Context.MODE_APPEND && writeOrAppendMode != Context.MODE_PRIVATE) {
            return false
        }

        //Log.d(TAG, "path="+context.getFilesDir().getAbsolutePath());
        try {
            /*
             * We have to use the openFileOutput()-method the ActivityContext
             * provides, to protect your file from others and This is done for
             * security-reasons. We chose MODE_WORLD_READABLE, because we have
             * nothing to hide in our file
             */
            val fOut = context.openFileOutput(fileName, writeOrAppendMode)
            val osw = OutputStreamWriter(fOut)
            // Write the string to the file
            osw.write(value)
            // save and close
            osw.flush()
            osw.close()
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun deleteFile(fileName: String?, context: Context) {
        context.deleteFile(fileName)
    }
}