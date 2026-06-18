package com.pauls.lorebound

/**
 * Application configuration singleton.
 * Developer mode is unlocked at runtime by tapping character name 7 times
 * and entering the password on the Character Sheet screen.
 */
object AppConfig {


    // Dev mode unlocked at runtime (not by config)
    var isDevModeUnlocked: Boolean = false
        private set

    fun unlockDevMode() {
        isDevModeUnlocked = true
    }

    fun lockDevMode() {
        isDevModeUnlocked = false
    }

    // Legacy — keep for backward compat but the 4th tab is now always visible as "Settings"
    val isDeveloperMode: Boolean
        get() = true // Tab always shows, content gated by isDevModeUnlocked

    val enableDeveloperTools: Boolean
        get() = isDevModeUnlocked

    val enableTimeSimulation: Boolean
        get() = isDevModeUnlocked

    val enableDebugActions: Boolean
        get() = isDevModeUnlocked

    const val DEV_PASSWORD = "Pauls1!"
}

