package com.pauls.lorebound.domain.chronicle

import com.pauls.lorebound.domain.service.TimeProvider
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages Chronicle seasonal availability.
 * Determines what state the Chronicle card should be in based on the current date.
 */
@Singleton
class ChronicleSeasonManager @Inject constructor(
    private val timeProvider: TimeProvider
) {

    fun getCurrentState(): ChronicleAvailabilityState {
        val today = LocalDate.parse(timeProvider.todayDate())
        val month = today.monthValue
        val day = today.dayOfMonth
        val year = today.year

        return when {
            // Dec 15 → Dec 31: Ready
            (month == 12 && day >= 15) -> ChronicleAvailabilityState.Ready(
                year = year,
                title = "YOUR $year CHRONICLE"
            )
            // Jan 1 → Jan 15: Ready (previous year)
            (month == 1 && day <= 15) -> ChronicleAvailabilityState.Ready(
                year = year - 1,
                title = "YOUR ${year - 1} CHRONICLE"
            )
            // Dec 1 → Dec 14: Preparing
            (month == 12 && day in 1..14) -> ChronicleAvailabilityState.Preparing(
                year = year,
                daysUntilReady = 15 - day
            )
            // Everything else: Hidden
            else -> ChronicleAvailabilityState.Hidden
        }
    }

    fun getChronicleYear(): Int {
        val today = LocalDate.parse(timeProvider.todayDate())
        return if (today.monthValue == 1 && today.dayOfMonth <= 15) {
            today.year - 1
        } else {
            today.year
        }
    }
}
