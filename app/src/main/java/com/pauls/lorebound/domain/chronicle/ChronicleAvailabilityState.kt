package com.pauls.lorebound.domain.chronicle

/**
 * Represents the current Chronicle availability state based on time of year.
 */
sealed class ChronicleAvailabilityState {

    /**
     * Jan 16 → Nov 30: Nothing shown on the quest page.
     */
    data object Hidden : ChronicleAvailabilityState()

    /**
     * Dec 1 → Dec 14: Small greyed out indicator — "Chronicle in the making"
     */
    data class Preparing(
        val year: Int,
        val daysUntilReady: Int
    ) : ChronicleAvailabilityState()

    /**
     * Dec 15 → Jan 15: Chronicle is ready to view — green active button.
     */
    data class Ready(
        val year: Int,
        val title: String
    ) : ChronicleAvailabilityState()
}
