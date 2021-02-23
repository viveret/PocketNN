package com.viveret.pocketn2.async.tasks

import android.os.AsyncTask
import com.viveret.pocketn2.async.AsyncTaskListener
import com.viveret.pocketn2.async.ObservableAsyncTask
import com.viveret.tinydnn.data.SaveConfig
import com.viveret.tinydnn.project.NeuralNetProject
import java.io.FileOutputStream
import java.io.OutputStream

class ProjectSaveAsyncTask(val project: NeuralNetProject):  AsyncTask<SaveConfig, Int, Collection<OutputStream>>(), ObservableAsyncTask<AsyncTask<SaveConfig, Int, Collection<OutputStream>>, AsyncTaskListener<AsyncTask<SaveConfig, Int, Collection<OutputStream>>, SaveConfig, Int, SaveConfig, OutputStream>, SaveConfig, Int, SaveConfig, OutputStream> {
    private lateinit var inputs: Collection<SaveConfig>

    override fun doInBackground(vararg params: SaveConfig?): Collection<OutputStream> {
        inputs = params.filterNotNull()
        return inputs.map { f -> this.project.save(f); FileOutputStream(f.outputFile) }
    }

    override fun onProgressUpdate(vararg values: Int?) {
        for (listener in this.listeners) {
            listener.onProgress(this, this.inputs.first(), values.filterNotNull().sum())
        }
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(results: Collection<OutputStream>?) {
        for (listener in this.listeners) {
            for (i in 0 until results!!.size) {
                listener.onDone(this, inputs.elementAt(i), inputs.elementAt(i))
            }
        }
        super.onPostExecute(results)
    }

    override fun addListener(listener: AsyncTaskListener<AsyncTask<SaveConfig, Int, Collection<OutputStream>>, SaveConfig, Int, SaveConfig, OutputStream>) {
        this.listeners.add(listener)
    }

    override fun removeListener(listener: AsyncTaskListener<AsyncTask<SaveConfig, Int, Collection<OutputStream>>, SaveConfig, Int, SaveConfig, OutputStream>) {
        this.listeners.remove(listener)
    }

    override val listeners: ArrayList<AsyncTaskListener<AsyncTask<SaveConfig, Int, Collection<OutputStream>>, SaveConfig, Int, SaveConfig, OutputStream>> = ArrayList()
}