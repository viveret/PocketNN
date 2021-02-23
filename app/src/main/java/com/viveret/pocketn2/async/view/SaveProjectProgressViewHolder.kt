package com.viveret.pocketn2.async.view

import android.os.AsyncTask
import android.view.View
import android.widget.ProgressBar
import com.viveret.pocketn2.async.AsyncTaskListener
import com.viveret.tinydnn.data.SaveConfig
import org.jetbrains.anko.runOnUiThread
import java.io.OutputStream

class SaveProjectProgressViewHolder : AsyncTaskListener<AsyncTask<SaveConfig, Int, Collection<OutputStream>>, SaveConfig, Int, SaveConfig, OutputStream> {
    private val progressBarTotal: ProgressBar

    constructor(progressBarTotal: ProgressBar) {
        this.progressBarTotal = progressBarTotal
    }

    override fun onDone(task: AsyncTask<SaveConfig, Int, Collection<OutputStream>>, params: SaveConfig, result: SaveConfig) {
        this.progressBarTotal.context.runOnUiThread {
            progressBarTotal.incrementProgressBy(1)
        }
    }

    override fun onError(task: AsyncTask<SaveConfig, Int, Collection<OutputStream>>, params: SaveConfig, exception: Exception) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun onInit(numFiles: Int) {
        this.progressBarTotal.context.runOnUiThread {
            progressBarTotal.max = numFiles
            progressBarTotal.visibility = View.VISIBLE
        }
    }

    override fun onStart(task: AsyncTask<SaveConfig, Int, Collection<OutputStream>>, params: SaveConfig) {
        this.progressBarTotal.context.runOnUiThread {
//            progressionView = progressionsView.add(params.url)
//            progressionView.show()
        }
    }

//    override fun onFileSizeKnown(sizeOfFile: Int) {
//        this.progressBarTotal.context.runOnUiThread {
//            progressionView.setMax(sizeOfFile)
//        }
//    }

    override fun onProgress(task: AsyncTask<SaveConfig, Int, Collection<OutputStream>>, params: SaveConfig, progress: Int) =
            this.progressBarTotal.context.runOnUiThread {
                progressBarTotal.progress += progress
            }
}