package com.viveret.pocketn2.async

import android.os.AsyncTask
import com.viveret.tinydnn.basis.BetterInputStream

interface FileListener<TOut>: AsyncTaskListener<AsyncTask<BetterInputStream, Int, Collection<TOut>>, BetterInputStream, Int, TOut, TOut> {
    fun onFileSizeKnown(params: BetterInputStream, sizeOfFile: Int)
}