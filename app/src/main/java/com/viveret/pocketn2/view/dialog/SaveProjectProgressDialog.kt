package com.viveret.pocketn2.view.dialog

import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.ViewGroup
import com.viveret.pocketn2.R
import com.viveret.pocketn2.async.AsyncTaskListener
import com.viveret.pocketn2.databinding.FragmentDialogProgressBinding
import com.viveret.pocketn2.view.model.DismissReason
import com.viveret.tinydnn.data.SaveConfig
import com.viveret.tinydnn.util.AppLifecycleContext
import com.viveret.tinydnn.util.async.OnItemSelectedListener
import java.io.OutputStream

class SaveProjectProgressDialog(context: AppLifecycleContext, val listener: OnItemSelectedListener):
        GenericProgressDialog(context), AsyncTaskListener<AsyncTask<SaveConfig, Int, Collection<OutputStream>>, SaveConfig, Int, SaveConfig, OutputStream> {
    override val minimizable: Boolean = true

    override val icon: Int
        get() = R.drawable.ic_menu_send

    override val title: String
        get() = "Save Project"

    private lateinit var config: SaveConfig
    private var wasMessageUpdated = false

    override val updatedMessage: String
        get() {
            this.wasMessageUpdated = false
            return this.config.toString()
        }

    override fun onStart(task: AsyncTask<SaveConfig, Int, Collection<OutputStream>>, params: SaveConfig) {
        this.config = params
        this.wasMessageUpdated = true
    }

    override fun onProgress(task: AsyncTask<SaveConfig, Int, Collection<OutputStream>>, params: SaveConfig, progress: Int) {
        this.progress = progress
    }

    override fun onDone(task: AsyncTask<SaveConfig, Int, Collection<OutputStream>>, params: SaveConfig, result: SaveConfig) {
        this.progress = this.max
        dismiss(DismissReason.Success)
        listener.onSelected(result).callback()
    }

    override fun onError(task: AsyncTask<SaveConfig, Int, Collection<OutputStream>>, params: SaveConfig, exception: Exception) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val messageWasUpdated: Boolean get() = this.wasMessageUpdated
    override val messageStringResId: Int = R.string.title_dialog_save_progress
    override val titleStringResId: Int = R.string.title_dialog_save_progress
}