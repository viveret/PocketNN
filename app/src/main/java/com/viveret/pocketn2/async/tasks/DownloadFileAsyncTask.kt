package com.viveret.pocketn2.async.tasks

import com.viveret.pocketn2.DataManager
import com.viveret.tinydnn.basis.BetterInputStream
import com.viveret.tinydnn.basis.DataSource
import java.io.OutputStream

class DownloadFileAsyncTask(dm: DataManager, saveForNextTime: Boolean, params: Array<BetterInputStream>) : CopyStreamAsyncTask(dm, if (saveForNextTime) DataSource.LocalFile else DataSource.TempFile, params.map { it.source }.toTypedArray()) {
    override fun beginCopy(stream: BetterInputStream) {
//        val url = stream.sourcePath(DataSource.RemoteFile)
//        try {
//            return HttpInputStream(URL(url), stream)
//        } catch (e: Exception) {
//            throw Exception("Cannot openData $url", e)
//        }
    }

    override fun finishCopy(stream: BetterInputStream, destination: OutputStream): OutputStream =
            destination

    override fun onProgressUpdate(vararg values: Int?) {
        val sum = values.filterNotNull().sum()
        this.listeners.forEach { x -> x.onProgress(this@DownloadFileAsyncTask, this@DownloadFileAsyncTask.streams.values.single(), sum) }
    }
}