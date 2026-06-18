package com.pauls.lorebound.domain.service

import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production time provider. Uses real system clock.
 */
@Singleton
class SystemTimeProvider @Inject constructor() : TimeProvider {

    override fun now(): Instant = Instant.now()

    override fun todayDate(): String =
        SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    override fun yesterdayDate(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        return SimpleDateFormat("yyyy-MM-dd", Locale.US).format(cal.time)
    }

    override fun addDays(date: String, days: Int): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val cal = Calendar.getInstance()
        cal.time = sdf.parse(date) ?: Date()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return sdf.format(cal.time)
    }
}

