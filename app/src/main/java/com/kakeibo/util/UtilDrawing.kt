package com.kakeibo.util

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import java.io.ByteArrayOutputStream

object UtilDrawing {

    fun createDefaultBitmap(w: Int, h: Int): Bitmap {
        val conf = Bitmap.Config.ARGB_8888
        return Bitmap.createBitmap(w, h, conf)
    }

    fun bitmapToBytes(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    fun bytesToBitmap(image: ByteArray?): Bitmap? {
        return image?.let {
            BitmapFactory.decodeByteArray(it, 0, image.size)
        }
    }

    fun bitmapToDrawalbe(context: Context, bitmap: Bitmap?): Drawable {
        return BitmapDrawable(context.resources, bitmap)
    }

    fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable
            if (bitmapDrawable.bitmap != null) {
                return bitmapDrawable.bitmap
            }
        }
        bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        }
        return bitmap
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height
        // CREATE A MATRIX FOR THE MANIPULATION
        val matrix = Matrix()
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight)

        // "RECREATE" THE NEW BITMAP
        val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
        bm.recycle()
        return resizedBitmap
    }

    fun getBitmapClippedCircle(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val path = Path()
        path.addCircle(
            (width / 2).toFloat(),
            (height / 2).toFloat(),
            Math.min(width, height / 2).toFloat(),
            Path.Direction.CCW
        )
        val canvas = Canvas(outputBitmap)
        canvas.clipPath(path)
        canvas.drawBitmap(bitmap, 0f, 0f, null)
        return outputBitmap
    }

    fun replaceColorExcept(src: Bitmap?, exceptColor: Int, targetColor: Int): Bitmap? {
        if (src == null) {
            return null
        }
        // Source image size
        val width = src.width
        val height = src.height
        val pixels = IntArray(width * height)
        //get pixels
        src.getPixels(pixels, 0, width, 0, 0, width, height)
        for (x in pixels.indices) {
            pixels[x] = if (pixels[x] != exceptColor) targetColor else pixels[x]
        }
        // create result bitmap output
        val result = Bitmap.createBitmap(width, height, src.config)
        //set pixels
        result.setPixels(pixels, 0, width, 0, 0, width, height)
        return result
    }

    fun getIconNameFromDrawableId(context: Context, drawableId: Int): String {
        return context.resources.getResourceEntryName(drawableId)
    }

    fun getDrawableIdFromIconName(context: Context, iconName: String?): Int {
        return context.resources.getIdentifier(iconName, "drawable", context.packageName)
    }
}