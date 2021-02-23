package com.viveret.pocketn2.view.widget

import android.view.View
import android.widget.Button
import com.viveret.pocketn2.R
import com.viveret.tinydnn.basis.Stream
import java.io.File

class OnlineAcknowledgementView(val listener: OnlineAcknowledgementListener) {
    val views = ArrayList<Button>()

    fun onCreateView(v: View) {
        val btnNo = v.findViewById<Button>(R.id.btnNo)
        val btnYes = v.findViewById<Button>(R.id.btnYes)
        val btnYesAndSave = v.findViewById<Button>(R.id.btnYesAndSave)

        views.addAll(arrayOf(btnNo, btnYes, btnYesAndSave))

        btnNo.setOnClickListener {
            disableView()
            listener.onlineDenied()
        }

        btnYes.setOnClickListener {
            disableView()
            listener.onlineAccepted()
        }

        btnYesAndSave.setOnClickListener {
            disableView()
            listener.onlineAcceptedAndSave()
        }
    }

    fun disableView() {
        for (v in views) {
            v.isEnabled = false
        }
    }

    interface OnlineAcknowledgementListener {
        fun onDownloaded(files: Map<Stream, File>)
        fun onlineDenied()
        fun onlineAccepted()
        fun onlineAcceptedAndSave()
    }
}