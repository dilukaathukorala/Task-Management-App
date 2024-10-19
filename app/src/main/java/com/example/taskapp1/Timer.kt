package com.example.taskapp1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

class Timer : AppCompatActivity() {

    private lateinit var timerTextView: TextView
    private lateinit var handler: Handler
    private var running = false
    private var offset: Long = 0
    private var updateInterval = 50L // Update every 50ms
    private val channelId = "timer_channel_id"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)

        createNotificationChannel() // Create notification channel

        timerTextView = findViewById(R.id.timerTextView)
        handler = Handler()

        val startButton = findViewById<Button>(R.id.startButton)
        val resetButton = findViewById<Button>(R.id.resetButton)
        val backButton = findViewById<ImageButton>(R.id.backButton)

        val defaultButtonColor = ContextCompat.getColor(this, R.color.blue)
        val redColor = ContextCompat.getColor(this, android.R.color.holo_red_light)

        startButton.setOnClickListener {
            if (!running) {
                startTimer()
                running = true
                startButton.text = "Stop"
                startButton.setBackgroundColor(redColor) // Change to red when running
                showNotification() // Show notification when timer starts
            } else {
                stopTimer()
                running = false
                startButton.text = "Start"
                startButton.setBackgroundColor(defaultButtonColor) // Change back to default
                cancelNotification() // Cancel notification when timer stops
            }
        }

        resetButton.setOnClickListener {
            resetTimer()
            running = false
            startButton.text = "Start"
            startButton.setBackgroundColor(defaultButtonColor) // Reset button color
            cancelNotification() // Cancel notification when timer is reset
        }

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // Change to your home activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // Clear activity stack
            startActivity(intent)
            finish() // Optional: Close current activity
        }
    }

    private fun createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val name = "Timer Notification"
            val descriptionText = "Notification for Timer"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Build the initial notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo) // Replace with your app icon or a custom icon
            .setContentTitle("Timer Running")
            .setContentText("Your timer is now running.")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1, notification) // Show the notification with ID 1
    }

    private fun updateNotification(timeText: String) {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager


        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Build the updated notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo) // Replace with your app icon or a custom icon
            .setContentTitle("Timer Running")
            .setContentText("Current time: $timeText") // Display current timer value
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true) // Prevent sound/vibration for updates
            .build()

        notificationManager.notify(1, notification) // Update the notification
    }

    private fun cancelNotification() {
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1) // Cancel the notification with ID 1
    }

    private fun startTimer() {
        handler.post(object : Runnable {
            override fun run() {
                if (running) {
                    val elapsedMillis = SystemClock.elapsedRealtime() - offset
                    val hours = (elapsedMillis / 3600000).toInt()
                    val minutes = ((elapsedMillis % 3600000) / 60000).toInt()
                    val seconds = ((elapsedMillis % 60000) / 1000).toInt()
                    val millis = (elapsedMillis % 1000) / 10 // Show milliseconds as two digits
                    val timeText = String.format("%02d:%02d:%02d:%02d", hours, minutes, seconds, millis)

                    timerTextView.text = timeText
                    updateNotification(timeText) // Update notification with current timer value
                    handler.postDelayed(this, updateInterval)
                }
            }
        })
        offset = SystemClock.elapsedRealtime() - offset
    }

    private fun stopTimer() {
        handler.removeCallbacksAndMessages(null)
        offset = SystemClock.elapsedRealtime() - offset
    }

    private fun resetTimer() {
        handler.removeCallbacksAndMessages(null)
        timerTextView.text = "00:00:00:00"
        offset = 0L // Reset the offset to 0
    }
}
