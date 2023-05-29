package com.example.moneytrackerapp.ui.homescreen

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar

object HomeScreenUtils {

    fun getDaysList(): List<String> {
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

    fun getWeeksList(): List<String> {
        val formatter = DateTimeFormatter.ofPattern("dd.MM")
        val endDate = LocalDate.of(2023, 12, 31)
        val monday = TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        var currDate = LocalDate.of(2020, 1, 1).with(monday)
        var nextDate = currDate.plusWeeks(1)
        val weeksList = mutableListOf<String>()
        while (currDate <= endDate) {
            val currDateStr = currDate.format(formatter)
            val nextDateStr = nextDate.format(formatter)
            val str = """
                ${currDateStr}-${nextDateStr}
                       ${nextDate.year}
            """.trimIndent()
            weeksList.add(str)
            currDate = nextDate
            nextDate = nextDate.plusWeeks(1)
        }
        return weeksList
    }

    fun getMonthsList(): List<String> {
        val formatter = DateTimeFormatter.ofPattern("MMM\nyyyy")
        val startDate = LocalDate.of(2010, 1, 1)
        val endDate = LocalDate.of(2023, 12, 1)
        val monthList = mutableListOf<String>()

        var currentDate = startDate
        while (currentDate <= endDate) {
            val formattedMonth = currentDate.format(formatter)
            monthList.add(formattedMonth)
            currentDate = currentDate.plusMonths(1)
        }

        return monthList
    }

    fun getCurrentDate() = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("dd.MM"))


}