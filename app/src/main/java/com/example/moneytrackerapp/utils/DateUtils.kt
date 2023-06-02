package com.example.moneytrackerapp.utils

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Calendar

object DateUtils {

      val MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMM\nyyyy")
      val DAY_FORMATTER = DateTimeFormatter.ofPattern("dd.MM")

    fun getDaysList(): List<String> {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        var currDate = LocalDate.ofYearDay(currentYear, 1)
        val dates = mutableListOf<String>()
        while (currDate.year == currentYear) {
            dates.add(currDate.format(DAY_FORMATTER).toString())
            currDate = currDate.plusDays(1)
        }
        return dates
    }

    fun getWeeksList(): List<String> {
        val endDate = LocalDate.of(2023, 12, 31)
        val monday = TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        var currDate = LocalDate.of(2020, 1, 1).with(monday)
        var nextDate = currDate.plusWeeks(1).minusDays(1)
        val weeksList = mutableListOf<String>()
        while (currDate <= endDate) {
            val str = getWeekStr(currDate, nextDate)
            weeksList.add(str)
            currDate = nextDate.plusDays(1)
            nextDate = nextDate.plusWeeks(1)
        }
        return weeksList
    }

    fun getMonthsList(): List<String> {
        val startDate = LocalDate.of(2010, 1, 1)
        val endDate = LocalDate.of(2023, 12, 1)
        val monthList = mutableListOf<String>()
        var currentDate = startDate
        while (currentDate <= endDate) {
            val formattedMonth = currentDate.format(MONTH_FORMATTER)
            monthList.add(formattedMonth)
            currentDate = currentDate.plusMonths(1)
        }
        return monthList
    }

    fun getCurrentDay(): String = LocalDate.now()
        .format(DateTimeFormatter.ofPattern("dd.MM"))

    fun getCurrentMonth(): String = LocalDate.now().format(MONTH_FORMATTER)

    fun getCurrentWeek(): String {
        val monday = TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)
        val currDate = LocalDate.now().with(monday)
        val nextDate = currDate.plusWeeks(1)
        return getWeekStr(currDate, nextDate)
    }

    private fun getWeekStr(currDate: LocalDate, nextDate: LocalDate): String {
        val currDateStr = currDate.format(DAY_FORMATTER)
        val nextDateStr = nextDate.format(DAY_FORMATTER)
        return """
                ${currDateStr}-${nextDateStr}
                       ${nextDate.year}
            """.trimIndent()
    }

    fun LocalDateTime.toMillis() = atZone(ZoneId.systemDefault())
        .toInstant()
        .toEpochMilli()

}