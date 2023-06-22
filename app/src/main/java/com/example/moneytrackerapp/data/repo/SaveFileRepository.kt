package com.example.moneytrackerapp.data.repo

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import com.example.moneytrackerapp.workers.SaveFileWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

interface SaveFileRepository {

    fun saveExpensesToFile(expenses: List<ExpenseTuple>)
}

@Singleton
class WorkerManagerSaveFileRepository @Inject constructor(
    @ApplicationContext ctxt: Context
) : SaveFileRepository {

    private val workManager = WorkManager.getInstance(ctxt)

    private val WORK_NAME = "save_file"

    override fun saveExpensesToFile(expenses: List<ExpenseTuple>) {
        val workBuilder = OneTimeWorkRequestBuilder<SaveFileWorker>()
        workBuilder.setInputData(generateWorkerInputData(expenses))
        workManager.enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            workBuilder.build()
        )
    }

    private fun generateWorkerInputData(expenses: List<ExpenseTuple>): Data {
        val builder = Data.Builder()
        return builder.putString(
            SaveFileWorker.EXPENSES_KEY,
            getOutputStr(expenses)
        ).build()
    }

    private fun getOutputStr(expenses: List<ExpenseTuple>): String {
        val sb = StringBuilder()
        expenses.forEach { sb.append(it.toString()).append("\n") }
        return sb.toString()
    }

}