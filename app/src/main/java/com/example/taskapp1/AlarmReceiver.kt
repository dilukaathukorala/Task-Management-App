package com.example.taskapp1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        private var ringtone: Ringtone? = null
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onReceive(context: Context, intent: Intent) {
        val reminderName = intent.getStringExtra("reminder_name") ?: "Default Reminder"
        startRingtone(context)
        createNotification(context, reminderName)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun startRingtone(context: Context) {
        val ringtoneUri = getCustomRingtone(context)
        ringtone = RingtoneManager.getRingtone(context, ringtoneUri)
        ringtone?.isLooping = true
        ringtone?.play()
    }

    private fun getCustomRingtone(context: Context): Uri {
        return Uri.parse("android.resource://${context.packageName}/raw/your_ringtone")
    }

    fun stopRingtone() {
        ringtone?.stop()
        ringtone = null
    }

    private fun createNotification(context: Context, reminderName: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(context)

        // Intent to stop the alarm
        val stopIntent = Intent(context, AlarmStopReceiver::class.java)
        stopIntent.putExtra("stop_ringtone", true)
        val stopPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "reminder_channel")
            .setSmallIcon(R.drawable.logo) // Replace with your app icon or a custom icon
            .setContentTitle("Reminder")
            .setContentText(reminderName)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(R.drawable.logo, "Stop Alarm", stopPendingIntent)

        notificationManager.notify(System.currentTimeMillis().toInt(), notification.build())
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "reminder_channel"
            val channelName = "Reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance)
            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}

