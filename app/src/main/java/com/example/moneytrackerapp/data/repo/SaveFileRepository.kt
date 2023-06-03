package com.example.moneytrackerapp.data.repo

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import com.example.moneytrackerapp.workers.SaveFileWorker

interface SaveFileRepository {

    fun saveExpensesToFile(expenses: List<ExpenseTuple>)
}


class WorkerManagerSaveFileRepository(ctxt: Context) : SaveFileRepository {

    private val workManager = WorkManager.getInstance(ctxt)

    override fun saveExpensesToFile(expenses: List<ExpenseTuple>) {
        val workBuilder = OneTimeWorkRequestBuilder<SaveFileWorker>()
        workBuilder.setInputData(generateWorkerInputData(expenses))
        workManager.enqueueUniqueWork(
            "save_file",
            ExistingWorkPolicy.REPLACE,
            workBuilder.build()
        )
    }

    private fun generateWorkerInputData(expenses: List<ExpenseTuple>): Data {
        val builder = Data.Builder()
        return builder.putString("expenses", getOutputStr(expenses)).build()
    }

    private fun getOutputStr(expenses: List<ExpenseTuple>): String {
        val sb = StringBuilder()
        expenses.forEach { sb.append(it.toString()).append("\n") }
        return sb.toString()
    }

}