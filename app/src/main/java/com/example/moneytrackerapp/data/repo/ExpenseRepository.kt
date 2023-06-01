package com.example.moneytrackerapp.data.repo

import com.example.moneytrackerapp.data.dao.ExpenseDao
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

interface ExpenseRepository {
    fun getExpensesByDate(range: Pair<LocalDateTime, LocalDateTime>): Flow<List<ExpenseTuple>>
}


class ExpenseRepositoryImpl(private val expenseDao: ExpenseDao) : ExpenseRepository {

    override fun getExpensesByDate(range: Pair<LocalDateTime, LocalDateTime>): Flow<List<ExpenseTuple>> {
        val startDate = range.first.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endDate = range.second.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return expenseDao.getAllByDate(startDate, endDate)
    }

}