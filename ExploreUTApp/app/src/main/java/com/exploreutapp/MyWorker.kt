package com.exploreutapp

import android.content.Context
import android.util.Log
import androidx.work.ListenableWorker

import androidx.work.Worker
import androidx.work.WorkerParameters


class MyWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    // A ListenableWorker is given a maximum of ten minutes to finish its execution
    // and return a ListenableWorker.Result. After this time has expired, the worker will be
    // signalled to stop and its ListenableFuture will be cancelled.
    override fun doWork(): ListenableWorker.Result {
        Log.d(TAG, "Performing long running task in scheduled job")
        // TODO(developer): add long running task here.
        return ListenableWorker.Result.success()
    }

    companion object {
        private val TAG = "MyWorker"
    }
}