package com.viveret.pocketn2.async.view

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import com.viveret.pocketn2.async.FileListener
import com.viveret.pocketn2.view.holders.ErrorViewHolder
import com.viveret.tinydnn.basis.BetterInputStream
import com.viveret.tinydnn.basis.DataSource
import org.jetbrains.anko.runOnUiThread
import java.io.OutputStream

class TrainDataProgressViewHolder : FileListener<OutputStream> {
    private val progressionsView: ProgressionListView
    private val progressBarTotal: ProgressBar
    private var errorView: ErrorViewHolder

    @SuppressLint("UseSparseArrays")
    private val progressionViews = HashMap<Int, ProgressionListView.ProgressionViewHolder>()

    constructor(progressionsView: ProgressionListView, progressBarTotal: ProgressBar, errorView: ErrorViewHolder) {
        this.progressionsView = progressionsView
        this.progressBarTotal = progressBarTotal
        this.errorView = errorView
    }

    constructor(progressionsView: ProgressionListView, progressBarTotal: ProgressBar):
            this(progressionsView, progressBarTotal, ErrorViewHolder(progressBarTotal.parent as ViewGroup))

    override fun onDone(task: AsyncTask<BetterInputStream, Int, Collection<OutputStream>>, params: BetterInputStream, result: OutputStream) {
        val progressionView = this.progressionViews[params.source.hashCode()]!!
        progressionView.view.context.runOnUiThread {
            progressBarTotal.incrementProgressBy(1)
            progressionView.done()
        }
    }

    override fun onError(task: AsyncTask<BetterInputStream, Int, Collection<OutputStream>>, params: BetterInputStream, exception: Exception) {
        this.errorView.item = exception
        this.errorView.refresh()
    }

    fun onInit(numFiles: Int) {
        this.progressBarTotal.context.runOnUiThread {
            progressBarTotal.max = numFiles
            progressBarTotal.visibility = View.VISIBLE
        }
    }

    override fun onStart(task: AsyncTask<BetterInputStream, Int, Collection<OutputStream>>, params: BetterInputStream) {
        this.progressBarTotal.context.runOnUiThread {
            val progressionView = progressionsView.add(params.source.sourcePath(DataSource.RemoteFile))
            progressionViews[params.source.hashCode()] = progressionView
            progressionView.show()
        }
    }

    override fun onFileSizeKnown(params: BetterInputStream, sizeOfFile: Int) {
        this.progressBarTotal.context.runOnUiThread {
            val progressionView = progressionViews[params.source.hashCode()]!!
            progressionView.setMax(sizeOfFile)
        }
    }

    override fun onProgress(task: AsyncTask<BetterInputStream, Int, Collection<OutputStream>>, params: BetterInputStream, progress: Int) =
            this.progressBarTotal.context.runOnUiThread {
                val progressionView = progressionViews[params.source.hashCode()]!!
                progressionView.progress(progress)
            }
}