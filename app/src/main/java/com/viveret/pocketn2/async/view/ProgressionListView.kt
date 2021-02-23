package com.viveret.pocketn2.async.view

import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.viveret.pocketn2.R
import com.viveret.pocketn2.databinding.WidgetProgressionBinding
import org.jetbrains.anko.runOnUiThread
import kotlin.math.roundToInt

class ProgressionListView(val view: LinearLayout) {
    private fun inflateProgressionView(): WidgetProgressionBinding =
            WidgetProgressionBinding.inflate(LayoutInflater.from(view.context), view, false)

    fun add(message: String): ProgressionViewHolder {
        val ret = ProgressionViewHolder(inflateProgressionView(), message)
        view.addView(ret.view)
        return ret
    }

    class ProgressionViewHolder(v: WidgetProgressionBinding, message: String) {
        val view: View = v.root
        private val progressBar = v.progress
        private val fileDownloadingTxt = v.fileDownloadingTxt
        private val messageTemplate: String
        private val url: String = message
        private var max: Int = 0
        private val textUpdater: TextUpdater

        init {
            this.messageTemplate = this.fileDownloadingTxt.text.toString().replace("%url%", this.url)
            this.textUpdater = TextUpdater(this.fileDownloadingTxt, this.messageTemplate)
        }

        fun setMax(max: Int) {
            this.view.context.runOnUiThread {
                if (max > 0) {
                    progressBar.max = max
                } else {
                    progressBar.isIndeterminate = true
                }
                this@ProgressionViewHolder.max = max
                progress(0)
            }
        }

        fun done() {
            progress(this.progressBar.progress - max)
        }

        var sum = 0
        fun progress(v: Int) {
            sum += v
            var msg = this.messageTemplate
            msg = msg.replace("%perc%", (if (this.max > 0) (this.progressBar.progress * 100.0 / this.max).roundToInt() else sum).toString())
            this.textUpdater.text = msg
            //msg = msg.replace("%speed%", this.speed)
            this.view.context.runOnUiThread {
                this@ProgressionViewHolder.textUpdater.run()
            }
            this.progressBar.incrementProgressBy(v)
        }

        fun show() {
            this.view.visibility = View.VISIBLE
        }

        private class TextUpdater(val view: TextView, var text: String) : Runnable {
            override fun run() {
                this.view.text = this.text
            }
        }
    }
}