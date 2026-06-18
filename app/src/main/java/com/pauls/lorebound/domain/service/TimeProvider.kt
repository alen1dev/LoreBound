package com.pauls.lorebound.domain.service

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Abstraction over system time.
 * All date-sensitive systems must use this instead of direct Date/Calendar calls.
 */
interface TimeProvider {
    fun now(): Instant
    fun todayDate(): String
    fun yesterdayDate(): String
    fun addDays(date: String, days: Int): String
}

