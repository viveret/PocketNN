package com.viveret.pocketn2.view.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.ortiz.touchview.TouchImageView
import com.viveret.tinydnn.data.graphics.LayerVizualization

class InspectPhotoDialog(private val context: Context) {
    fun show(maxWidth: Int, maxHeight: Int, imgBmp: LayerVizualization) {
        val builder = Dialog(context)
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val imgViewDialog = TouchImageView(context)
        val lpImageHighRes = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT)
        imgViewDialog.layoutParams = lpImageHighRes
        val bmp = imgBmp.asBitmapAtLeast(maxWidth, maxHeight)
        val canvasBmp = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ARGB_8888)
        val canvasToShow = Canvas(canvasBmp)
        canvasToShow.drawColor(Color.TRANSPARENT)
        imgViewDialog.setImageBitmap(canvasBmp)
        imgViewDialog.scaleType = ImageView.ScaleType.CENTER_CROP

        if (bmp.width > bmp.height) {
            canvasToShow.drawBitmap(bmp, 0.0f, (maxHeight - bmp.height) / 2.0f, null)
        } else {
            canvasToShow.drawBitmap(bmp, (maxWidth - bmp.width) / 2.0f, 0.0f, null)
        }

        builder.addContentView(imgViewDialog, RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT))

        builder.show()
    }
}