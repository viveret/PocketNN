package com.viveret.pocketn2.asyncFile

import android.content.Context
import android.os.AsyncTask
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.async.AsyncTaskQueue
import com.viveret.pocketn2.async.FileListener
import com.viveret.pocketn2.async.tasks.DownloadFileAsyncTask
import com.viveret.tinydnn.basis.BetterInputStream
import com.viveret.tinydnn.basis.DataSource
import com.viveret.tinydnn.basis.Stream
import com.viveret.tinydnn.basis.StreamPackage
import java.io.OutputStream

class StreamDownloadQueue: AsyncTaskQueue<AsyncTask<BetterInputStream, Int, Collection<OutputStream>>, BetterInputStream, Int, OutputStream, OutputStream>, FileListener<OutputStream> {
    override fun onFileSizeKnown(params: BetterInputStream, sizeOfFile: Int) {
        for (listener in listeners.filterIsInstance<FileListener<OutputStream>>()) {
            listener.onFileSizeKnown(params, sizeOfFile)
        }
    }

    val saveForNextTime: Boolean
    val dm: DataManager

    constructor(suite: StreamPackage, saveForNextTime: Boolean, context: Context, onTaskQueueComplete: (mapped: Map<BetterInputStream, OutputStream>) -> Unit) : this(
            suite.streams.values.groupBy { g -> g.id }.map { g -> g.value.first() }.toTypedArray(),
            saveForNextTime, context, onTaskQueueComplete)

    constructor(params: Array<BetterInputStream>, saveForNextTime: Boolean, context: Context, onTaskQueueComplete: (mapped: Map<BetterInputStream, OutputStream>) -> Unit) : super(params, onTaskQueueComplete) {
        this.saveForNextTime = saveForNextTime
        this.dm = DataManager.get(context)
    }

    constructor(params: Array<Stream>, saveForNextTime: Boolean, context: Context, onTaskQueueComplete: (mapped: Map<BetterInputStream, OutputStream>) -> Unit) :
            this(params.map { it.sourceStream(DataSource.RemoteFile) }.toTypedArray(), saveForNextTime, context, onTaskQueueComplete)

    override fun execute(params: BetterInputStream): AsyncTask<BetterInputStream, Int, Collection<OutputStream>> {
        val ret = DownloadFileAsyncTask(dm, saveForNextTime, arrayOf(params))
        ret.addListener(this)
        ret.execute(params)
        return ret
    }
}
