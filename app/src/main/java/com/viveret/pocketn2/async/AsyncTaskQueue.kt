package com.viveret.pocketn2.async

import android.annotation.SuppressLint
import android.os.AsyncTask

abstract class AsyncTaskQueue<TAsyncTask, Params, Progress, Result, TaskResult>(val params: Array<Params>, val onTaskQueueComplete: (mapped: Map<Params, Result>) -> Unit):
        ObservableAsyncTask<TAsyncTask, AsyncTaskListener<TAsyncTask, Params, Progress, Result, TaskResult>, Params, Progress, Result, TaskResult>,
        AsyncTaskListener<TAsyncTask, Params, Progress, Result, TaskResult>
        where TAsyncTask: AsyncTask<Params, Progress, Collection<TaskResult>> {

    private var taskIndex = 0

    @SuppressLint("UseSparseArrays")
    private val taskResults = HashMap<Int, Result>()

    abstract fun execute(params: Params): TAsyncTask

    override fun addListener(listener: AsyncTaskListener<TAsyncTask, Params, Progress, Result, TaskResult>) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener)
        }
    }

    override fun removeListener(listener: AsyncTaskListener<TAsyncTask, Params, Progress, Result, TaskResult>) {
        this.listeners.remove(listener)
    }

    override val listeners = ArrayList<AsyncTaskListener<TAsyncTask, Params, Progress, Result, TaskResult>>()

    override fun onStart(task: TAsyncTask, params: Params) =
            this.listeners.forEach { x -> x.onStart(task, params) }

    override fun onProgress(task: TAsyncTask, params: Params, progress: Progress) =
            this.listeners.forEach { x -> x.onProgress(task, params, progress) }

    override fun onDone(task: TAsyncTask, params: Params, result: Result) {
        taskResults[params.hashCode()] = result
        this.listeners.forEach { x -> x.onDone(task, params, result) }
        nextTaskOrDone()
    }

    private fun nextTaskOrDone() {
        val runAllAtOnce = true
        if (runAllAtOnce) {
            when {
                taskIndex == 0 -> {
                    taskIndex = -1 // Go negative
                    for (taskToExecute in params) {
                        this.execute(taskToExecute)
                    }
                }
                taskIndex > -params.size -> taskIndex--
                else -> {
                    val mapped = params.mapIndexed { _, x -> x to taskResults[x.hashCode()]!! }.toMap()
                    this.onTaskQueueComplete(mapped)
                }
            }
        } else {
            if (taskIndex < params.size) {
                val curIndex = this.taskIndex
                taskIndex++
                this.execute(params[curIndex])
            } else {
                val mapped = params.mapIndexed { _, x -> x to taskResults[x.hashCode()]!! }.toMap()
                this.onTaskQueueComplete(mapped)
            }
        }
    }

    fun start() {
        if (this.taskIndex == 0) {
            this.nextTaskOrDone()
        } else {
            throw Exception("AsyncTaskQueue already started")
        }
    }

    override fun onError(task: TAsyncTask, params: Params, exception: Exception) =
            this.listeners.forEach { x -> x.onError(task, params, exception) }
}