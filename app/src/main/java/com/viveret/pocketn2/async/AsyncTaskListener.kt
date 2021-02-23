package com.viveret.pocketn2.async

import android.os.AsyncTask

interface AsyncTaskListener<TAsyncTask, Params, Progress, Result, TaskResult> where TAsyncTask: AsyncTask<Params, Progress, Collection<TaskResult>> {
    fun onStart(task: TAsyncTask, params: Params)
    fun onProgress(task: TAsyncTask, params: Params, progress: Progress)
    fun onDone(task: TAsyncTask, params: Params, result: Result)
    fun onError(task: TAsyncTask, params: Params, exception: Exception)
}