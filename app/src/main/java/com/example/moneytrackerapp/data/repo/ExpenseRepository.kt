package com.example.moneytrackerapp.data.repo

import com.example.moneytrackerapp.data.dao.ExpenseDao
import com.example.moneytrackerapp.data.entity.ExpenseTuple
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

interface ExpenseRepository {
    fun getExpensesByDate(date: LocalDate): Flow<List<ExpenseTuple>>
}


class ExpenseRepositoryImpl(private val expenseDao: ExpenseDao) : ExpenseRepository {

    override fun getExpensesByDate(date: LocalDate): Flow<List<ExpenseTuple>> {
        val startOfDay = LocalDateTime.of(date, LocalTime.MIN)
        val endOfDay = LocalDateTime.of(date, LocalTime.MAX)
        val startDate = startOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val endDate = endOfDay.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return expenseDao.getAllByDate(startDate, endDate)
    }

}