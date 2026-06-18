package com.pauls.lorebound.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.*
import com.pauls.lorebound.MainActivity
import com.pauls.lorebound.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Weekly notification worker that fires every Friday at 6 PM.
 * Reminds the user to check new quests and keep their lore path going.
 */
@HiltWorker
class WeeklyQuestReminderWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Create channel (required for API 26+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Quest Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Weekly reminders to check new quests and lore paths"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent to open the app
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Rotate between motivational messages
        val messages = listOf(
            "New quests await, Wanderer. Your lore path grows cold without you.",
            "Friday eve — fresh quests have arrived. Time to write your story.",
            "Your character's journey pauses without you. New challenges await.",
            "The weekly quests are ready. Keep your streak alive, Adventurer.",
            "New paths have opened. Your lore beckons — don't let it fade."
        )
        val message = messages[System.currentTimeMillis().mod(messages.size)]

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚔ New Quests Available")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "lorebound_quest_reminders"
        const val NOTIFICATION_ID = 1001
        const val WORK_NAME = "weekly_quest_reminder"
    }
}

/**
 * Schedules the weekly Friday 6 PM notification.
 * Also schedules the annual Dec 15 Chronicle notification.
 */
object QuestReminderScheduler {

    fun schedule(context: Context) {
        scheduleWeeklyReminder(context)
        scheduleChronicleNotification(context)
    }

    private fun scheduleWeeklyReminder(context: Context) {
        val delay = calculateDelayToNextFriday6PM()

        val workRequest = PeriodicWorkRequestBuilder<WeeklyQuestReminderWorker>(
            7, TimeUnit.DAYS
        )
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(true)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WeeklyQuestReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    private fun scheduleChronicleNotification(context: Context) {
        val delay = calculateDelayToDec15()
        if (delay <= 0) return // Already past Dec 15 this year, skip

        val workRequest = OneTimeWorkRequestBuilder<ChronicleReadyWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            ChronicleReadyWorker.WORK_NAME,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WeeklyQuestReminderWorker.WORK_NAME)
        WorkManager.getInstance(context).cancelUniqueWork(ChronicleReadyWorker.WORK_NAME)
    }

    /**
     * Calculate milliseconds until next Friday at 18:00.
     */
    private fun calculateDelayToNextFriday6PM(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY)
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If we're past Friday 6 PM this week, target next week
        if (target.timeInMillis <= now.timeInMillis) {
            target.add(Calendar.WEEK_OF_YEAR, 1)
        }

        return target.timeInMillis - now.timeInMillis
    }

    /**
     * Calculate milliseconds until Dec 15 at 10:00 AM this year.
     * Returns negative if already past.
     */
    private fun calculateDelayToDec15(): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.MONTH, Calendar.DECEMBER)
            set(Calendar.DAY_OF_MONTH, 15)
            set(Calendar.HOUR_OF_DAY, 10)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        return target.timeInMillis - now.timeInMillis
    }
}

/**
 * One-time worker that fires on Dec 15 to notify about Chronicle availability.
 */
@HiltWorker
class ChronicleReadyWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        showNotification()
        return Result.success()
    }

    private fun showNotification() {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Chronicle",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Your yearly Chronicle is ready"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 1, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("✦ Your Chronicle is Here")
            .setContentText("Your year-in-review awaits. Tap to relive your story.")
            .setStyle(NotificationCompat.BigTextStyle().bigText(
                "Your Chronicle is ready! Relive the quests you conquered, the lore you wrote, and the character you became this year."
            ))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val CHANNEL_ID = "lorebound_chronicle"
        const val NOTIFICATION_ID = 1002
        const val WORK_NAME = "chronicle_ready_notification"
    }
}



