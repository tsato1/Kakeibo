package com.kakeibo.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class UtilFiles {
    private static final String TAG = UtilFiles.class.getSimpleName();

    public static boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    public static String getFileValue(String fileName, Context context) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            String inputLine = "";
            /*
             * We have to use the openFileInput()-method the ActivityContext
             * provides. Again for security reasons with openFileInput(...)
             */
            FileInputStream fIn = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader inBuff = new BufferedReader(isr);
            while ((inputLine = inBuff.readLine()) != null) {
                stringBuilder.append(inputLine);
                stringBuilder.append("\n");
            }
            inBuff.close();
            return stringBuilder.toString();
        } catch (IOException e) {
            return null;
        }
    }

    public static ArrayList<String> getFileValues(String fileName, Context context) {
        try {
            StringBuilder sb = new StringBuilder();
            String inputLine = "";
            FileInputStream fIn = context.openFileInput(fileName);
            InputStreamReader isr = new InputStreamReader(fIn);
            BufferedReader inBuff = new BufferedReader(isr);
            while ((inputLine = inBuff.readLine()) != null) {
                sb.append(inputLine);
                sb.append("\n");
            }
            inBuff.close();
            return new ArrayList<>(Arrays.asList(sb.toString().split("\t")));
        } catch (IOException e) {
            return null;
        }
    }

    public static boolean appendFileValue(String fileName, String value, Context context) {
        return writeToFile(fileName, value, context, Context.MODE_APPEND);
    }

    public static boolean setFileValue(String fileName, String value, Context context) {
        return writeToFile(fileName, value, context, Context.MODE_WORLD_READABLE);
    }

    public static boolean writeToFile(String fileName, String value, Context context,
                                      int writeOrAppendMode) {
        // just make sure it's one of the modes we support
        // Context.MODE_PRIVATE: erase existing file
        // Context.MODE_APPEND: appends to an existing file
        if (writeOrAppendMode != Context.MODE_APPEND && writeOrAppendMode != MODE_PRIVATE) {
            return false;
        }

        //Log.d(TAG, "path="+context.getFilesDir().getAbsolutePath());

        try {
            /*
             * We have to use the openFileOutput()-method the ActivityContext
             * provides, to protect your file from others and This is done for
             * security-reasons. We chose MODE_WORLD_READABLE, because we have
             * nothing to hide in our file
             */
            FileOutputStream fOut = context.openFileOutput(fileName, writeOrAppendMode);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);
            // Write the string to the file
            osw.write(value);
            // save and close
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void deleteFile(String fileName, Context context) {
        context.deleteFile(fileName);
    }
}
