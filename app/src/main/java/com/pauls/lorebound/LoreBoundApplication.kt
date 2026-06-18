package com.pauls.lorebound

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.pauls.lorebound.notifications.QuestReminderScheduler
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class LoreBoundApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        // Schedule the weekly Friday 6 PM quest reminder (if user hasn't disabled it)
        val prefs = getSharedPreferences("lorebound_settings", MODE_PRIVATE)
        if (prefs.getBoolean("notifications_enabled", true)) {
            QuestReminderScheduler.schedule(this)
        }
    }
}
