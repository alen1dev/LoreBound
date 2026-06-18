package com.pauls.lorebound.domain.service

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Debug time provider for developer mode.
 * Allows simulating time advancement for testing long-term features.
 */
@Singleton
class DebugTimeProvider @Inject constructor() : TimeProvider {

    private var offsetDays: Long = 0L

    override fun now(): Instant = Instant.now().plus(offsetDays, ChronoUnit.DAYS)

    override fun todayDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, offsetDays.toInt())
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    override fun yesterdayDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, offsetDays.toInt() - 1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    override fun addDays(date: String, days: Int): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.time = sdf.parse(date) ?: Date()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return sdf.format(cal.time)
    }

    // ── Debug controls ──────────────────────────────

    fun advanceDays(days: Int) {
        offsetDays += days
    }

    fun advanceWeeks(weeks: Int) {
        offsetDays += weeks * 7L
    }

    fun advanceMonths(months: Int) {
        offsetDays += months * 30L
    }

    fun advanceYears(years: Int) {
        offsetDays += years * 365L
    }

    fun resetTime() {
        offsetDays = 0L
    }

    fun setToDate(targetDate: String) {
        val today = java.time.LocalDate.now()
        val target = java.time.LocalDate.parse(targetDate)
        offsetDays = java.time.temporal.ChronoUnit.DAYS.between(today, target)
    }

    fun getOffsetDays(): Long = offsetDays
}

