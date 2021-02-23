package com.viveret.pocketn2.async

import android.os.AsyncTask

interface ObservableAsyncTask<TAsyncTask, TAsyncTaskListener, Params, Progress, Result, TaskResult> where TAsyncTask : AsyncTask<Params, Progress, Collection<TaskResult>>, TAsyncTaskListener: AsyncTaskListener<TAsyncTask, Params, Progress, Result, TaskResult> {
    fun addListener(listener: TAsyncTaskListener)
    fun removeListener(listener: TAsyncTaskListener)
    val listeners: ArrayList<TAsyncTaskListener>
}