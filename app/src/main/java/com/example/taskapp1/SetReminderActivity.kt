package com.example.taskapp1

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Calendar

class SetReminderActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_reminder)

        timePicker = findViewById(R.id.timePicker)

        // Initialize the back button
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener {
            finish() // Close the current activity
        }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.setReminderButton).setOnClickListener {
            val reminderName = findViewById<EditText>(R.id.reminderNameEditText).text.toString()
            setReminder(reminderName)
        }
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun setReminder(reminderName: String) {
        if (reminderName.isEmpty()) {
            Toast.makeText(this, "Please enter a reminder name", Toast.LENGTH_SHORT).show()
            return
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timePicker.hour)
            set(Calendar.MINUTE, timePicker.minute)
            set(Calendar.SECOND, 0)
        }

        if (calendar.timeInMillis > System.currentTimeMillis()) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(this, AlarmReceiver::class.java).apply {
                putExtra("reminder_name", reminderName)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Set exact and allow while idle
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

            Toast.makeText(this, "Reminder set successfully!", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "Please set a time in the future", Toast.LENGTH_SHORT).show()
        }
    }

}
