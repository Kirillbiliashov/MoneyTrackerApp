package com.example.moneytrackerapp

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

object HomeScreenUtils {

    fun getDateRange(): List<String> {
        val datePattern = DateTimeFormatter.ofPattern("dd.MM.y")
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val startDate = LocalDate.parse("01.01.${currentYear}", datePattern)
        val endDate = LocalDate.parse("31.12.${currentYear}", datePattern)
        var currDate = startDate
        val dates = mutableListOf<String>()
        while (currDate <= endDate) {
            dates.add(currDate.format(datePattern).substring(0, 5))
            currDate = currDate.plusDays(1)
        }
        return dates
    }

    fun getCurrentDate(): String {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.y")).substring(0, 5)
    }

}