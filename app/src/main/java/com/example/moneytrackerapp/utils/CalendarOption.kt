package com.example.moneytrackerapp.utils

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.YearMonth

enum class CalendarOption {
    DAILY {
        override val datesList: List<String> by lazy { DateUtils.getDaysList() }
        override val currentDate: String by lazy { DateUtils.getCurrentDay() }
        override fun parseDateStr(date: String): Pair<LocalDateTime, LocalDateTime> {
            val splitDate = date.split(".")
            val localDate = LocalDate.of(2023, splitDate[1].toInt(),
                splitDate[0].toInt())
            val startOfDay = LocalDateTime.of(localDate, LocalTime.MIN)
            val endOfDay = LocalDateTime.of(localDate, LocalTime.MAX)
            return startOfDay to endOfDay
        }
    },
    WEEKLY {
        override val datesList: List<String> by lazy { DateUtils.getWeeksList() }
        override val currentDate: String by lazy { DateUtils.getCurrentWeek() }
        override fun parseDateStr(date: String): Pair<LocalDateTime, LocalDateTime> {
            val stringParts = date.split("\n")
            val daysRangeStr = stringParts[0].trim()
            val year = stringParts[1].trim().toInt()
            val splitRange = daysRangeStr.split("-")
            val firstDatePair = getDayMonthPair(splitRange[0])
            val secondDatePair = getDayMonthPair(splitRange[1])
            val firstDay = LocalDate.of(year, firstDatePair.second, firstDatePair.first)
            val lastDay = LocalDate.of(year, secondDatePair.second, secondDatePair.first)
            val startOfWeek = LocalDateTime.of(firstDay, LocalTime.MIN)
            val endOfWeek = LocalDateTime.of(lastDay, LocalTime.MAX)
            return startOfWeek to endOfWeek
        }

        private fun getDayMonthPair(date: String): Pair<Int, Int> {
            val splitDate = date.split(".")
            return splitDate[0].toInt() to splitDate[1].toInt()
        }
    },
    MONTHLY {
        override val datesList: List<String> by lazy { DateUtils.getMonthsList() }
        override val currentDate: String by lazy { DateUtils.getCurrentMonth() }
        override fun parseDateStr(date: String): Pair<LocalDateTime, LocalDateTime> {
            val stringParts = date.split("\n")
            val monthStr = stringParts[0]
            val year = stringParts[1].toInt()
            val month = Month.values().first { it.toString().startsWith(monthStr.uppercase()) }
            val days = YearMonth.of(year, month).lengthOfMonth()
            val firstDay = LocalDate.of(year, month, 1)
            val lastDay = LocalDate.of(year, month, days)
            val startOfMonth = LocalDateTime.of(firstDay, LocalTime.MIN)
            val endOfMonth = LocalDateTime.of(lastDay, LocalTime.MAX)
            return startOfMonth to endOfMonth
        }
    };

    abstract val datesList: List<String>
    abstract val currentDate: String
    abstract fun parseDateStr(date: String): Pair<LocalDateTime, LocalDateTime>
}