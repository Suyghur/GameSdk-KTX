package cn.flyfun.gamesdk.core.utils

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import cn.flyfun.support.ResUtils

/**
 * @author #Suyghur.
 * Created on 3/26/21
 */
object ScreenShotUtils {

    fun getScreenShot(activity: Activity): Bitmap {
        val view = activity.window.decorView
        view.isDrawingCacheEnabled = true
        view.buildDrawingCache()
        return view.drawingCache
    }

    fun getScreenShot2(activity: Activity): Bitmap {
        val view = activity.window.decorView
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas()
        canvas.setBitmap(bitmap)
        view.draw(canvas)
        return bitmap
    }

    fun joinQRCode(context: Context, background: Bitmap): Bitmap {
        val foreground = BitmapFactory.decodeResource(context.resources, ResUtils.getResId(context, "qrcode", "drawable"))
        val bgWidth = background.width
        val bgHeight = background.height
        val fgWidth = foreground.width
        val fgHeight = foreground.height
        val newBitmap = Bitmap.createBitmap(bgWidth, bgHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(newBitmap)
        canvas.drawBitmap(background, 0f, 0f, null)
        canvas.drawBitmap(foreground, (bgWidth - fgWidth).toFloat(), (bgHeight - fgHeight).toFloat(), null)
        canvas.save()
        canvas.restore()
        if (!background.isRecycled) {
            background.recycle()
        }
        if (!foreground.isRecycled) {
            foreground.recycle()
        }
        return newBitmap
    }
}