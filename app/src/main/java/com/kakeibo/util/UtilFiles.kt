package com.kakeibo.util

import android.content.Context
import com.kakeibo.core.data.local.entities.CategoryEntity
import com.kakeibo.core.data.local.entities.ItemEntity
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*

object UtilFiles {
    val FILE_NAME = "to_be_exported"

    fun fileExists(context: Context, filename: String?): Boolean {
        val file = context.getFileStreamPath(filename)
        return !(file == null || !file.exists())
    }

    fun getFileValue(fileName: String, context: Context): String? {
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
            ArrayList(listOf(*sb.toString().split("\t").toTypedArray()))
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

    /* Used in Import functionality */
    fun readCsvToList(
        list: MutableList<ItemEntity>,
        reader: BufferedReader,
        allCategoriesMap: Map<Int, CategoryEntity>
    ): String {
        var counter = 0
        val stringBuilder = StringBuilder()
        /*
         0: ordered by Date
            Event Date,Amount,Category,Memo,Updated Date,Category Code
         1: ordered by Category
            Category,Amount,Memo,Event Date,Updated Date,Category Code
         */
        reader.lineSequence().forEach {
            if (counter == 0) {
                // skip the header. it's written in different languages
            } else {
                val line = it.split(",")

                if (!UtilDate.isYMDDateValid(line[0])) {
                    stringBuilder.append("EventDate is not valid at line $counter \n")
                }
                if (!isAmountValid(line[1])) {
                    stringBuilder.append("Amount is not valid at line $counter \n")
                }
                if (!UtilCategory.isCategoryValid(line[5], line[2], allCategoriesMap)) {
                    stringBuilder.append("Category is not valid at line $counter \n")
                }
                if (!UtilDate.isYMDHSDateValid(line[4])) {
                    stringBuilder.append("UpdatedDate is not valid at line $counter \n")
                }

//                list.add(
//                    ItemEntity(
//                        BigDecimal(line[1].trim()), //amount
//                        "",
//                        line[5].trim().toInt(), // category code
//                        line[3].trim(), // memo
//                        line[0].trim(), // event date
//                        line[4].trim()  // updated date
//                    )
//                )
            }

            counter++
        }

        return stringBuilder.toString()
    }

    private fun isAmountValid(amount: String): Boolean {
        if (amount.toIntOrNull() == null)
            return false

        if (10 < amount.length)
            return false

        return true
    }
}