package com.example.taskapp1

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
class AlarmStopReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val stopRingtone = intent.getBooleanExtra("stop_ringtone", false)

        if (stopRingtone) {
            // Stop the ringtone through the singleton instance
            AlarmReceiver().stopRingtone()

            // Cancel the alarm if necessary
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val stopIntent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                0,
                stopIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            alarmManager.cancel(pendingIntent)

            Log.d("AlarmStopReceiver", "Alarm stopped via notification")
        }
    }
}

