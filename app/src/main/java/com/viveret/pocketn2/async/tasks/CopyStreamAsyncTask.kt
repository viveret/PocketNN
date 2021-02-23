package com.viveret.pocketn2.async.tasks

import android.os.AsyncTask
import android.util.Log
import com.viveret.pocketn2.DataManager
import com.viveret.pocketn2.async.AsyncTaskException
import com.viveret.pocketn2.async.AsyncTaskListener
import com.viveret.pocketn2.async.FileListener
import com.viveret.pocketn2.async.ObservableAsyncTask
import com.viveret.tinydnn.basis.BetterInputStream
import com.viveret.tinydnn.basis.DataSource
import com.viveret.tinydnn.basis.Stream
import java.io.OutputStream
import kotlin.math.max
import kotlin.math.min

abstract class CopyStreamAsyncTask(val dm: DataManager, val dataSource: DataSource, val params: Array<Stream>) :
        AsyncTask<BetterInputStream, Int, Collection<OutputStream>>(), ObservableAsyncTask<AsyncTask<BetterInputStream, Int, Collection<OutputStream>>,
        AsyncTaskListener<AsyncTask<BetterInputStream, Int, Collection<OutputStream>>, BetterInputStream, Int, OutputStream, OutputStream>, BetterInputStream, Int, OutputStream, OutputStream> {

    val results = HashMap<Int, OutputStream>()
    val streams = HashMap<Int, BetterInputStream>()

    abstract fun beginCopy(stream: BetterInputStream)
    abstract fun finishCopy(stream: BetterInputStream, destination: OutputStream): OutputStream

    override fun addListener(listener: AsyncTaskListener<AsyncTask<BetterInputStream, Int, Collection<OutputStream>>, BetterInputStream, Int, OutputStream, OutputStream>) {
        this.listeners.add(listener)
    }

    override fun removeListener(listener: AsyncTaskListener<AsyncTask<BetterInputStream, Int, Collection<OutputStream>>, BetterInputStream, Int, OutputStream, OutputStream>) {
        this.listeners.remove(listener)
    }

    override val listeners = ArrayList<AsyncTaskListener<AsyncTask<BetterInputStream, Int, Collection<OutputStream>>, BetterInputStream, Int, OutputStream, OutputStream>>()

    override fun doInBackground(vararg params: BetterInputStream): Collection<OutputStream>? {
        var i = 0
        return try {
            val ret = Array(min(this.params.size, params.size)) {
                i = it
                val stream = params[it]
                streams[stream.source.hashCode()] = stream
                this.copyStream(stream)
            }
            ret.toList()
        } catch (e: Exception) {
            for (i2 in 0..i) {
                this.params[i2].delete(DataSource.LocalFile)
            }
            null
        }
    }

    private fun copyStream(stream: BetterInputStream): OutputStream = try {
        val destination = stream.source.destinationStream(dataSource)
        this.listeners.forEach { x -> x.onStart(this@CopyStreamAsyncTask, stream) }
        (stream.currentStream).use {
            beginCopy(stream)
            val sizeOfStream = stream.size

            if (sizeOfStream != 0) {
                this.listeners.filterIsInstance<FileListener<OutputStream>>().forEach { x -> x.onFileSizeKnown(stream, sizeOfStream) }
                val chunkSize = min(4096, max(256, if (sizeOfStream < 0) 4096 else sizeOfStream))
                val buf = ByteArray(chunkSize)

                var i = 0
                var n = 1
                val streamSizeMax = if (sizeOfStream >= 0) sizeOfStream else (1000 * 1000 * 1000) // 1gb
                while (i < streamSizeMax && n > 0) {
                    n = it.read(buf)
                    if (n > 0) {
                        i += n
                        destination.write(buf, 0, n)
                        publishProgress(n)
                    } else {
                        i = streamSizeMax
                    }
                }
            }

            destination.flush()
            destination.close()

            val result = finishCopy(stream, destination)
            results[stream.source.hashCode()] = result
            this.listeners.forEach { x -> x.onDone(this@CopyStreamAsyncTask, stream, result) }
            return destination
        }
    } catch (e: Exception) {
        Log.e("com.viveret.pocketn2", "Could not do in background", e)
        stream.source.delete(DataSource.LocalFile)
        this.listeners.forEach { x -> x.onError(this@CopyStreamAsyncTask, stream, e) }
        throw AsyncTaskException(e)
    }

    override fun onProgressUpdate(vararg values: Int?) {
        val sum = values.filterNotNull().sum()
        this.listeners.forEach { x -> x.onProgress(this@CopyStreamAsyncTask, this@CopyStreamAsyncTask.streams.values.first(), sum) }
    }
}