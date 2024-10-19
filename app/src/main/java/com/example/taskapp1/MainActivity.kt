package com.example.taskapp1

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.taskapp1.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.appwidget.AppWidgetManager
import android.content.ComponentName

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val noteKey = "note"  // Key for storing the note in SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize BottomNavigationView and handle item selection
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_tasks -> {
                    startActivity(Intent(this, TaskList::class.java))
                    true
                }
                R.id.nav_timer -> {
                    startActivity(Intent(this, Timer::class.java))
                    true
                }
                R.id.nav_Alarm -> {
                    startActivity(Intent(this, SetReminderActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("NoteData", MODE_PRIVATE)

        // Save Note Button (Create/Update operation)
        binding.saveNoteButton.setOnClickListener {
            val note = binding.notesEditText.text.toString()
            if (note.isNotEmpty()) {
                sharedPreferences.edit().putString(noteKey, note).apply()
                Toast.makeText(this, "Note Saved Successfully", Toast.LENGTH_SHORT).show()
                binding.notesEditText.text?.clear()  // Clear input field

                // Update the widget after saving the note
                updateWidget()
            } else {
                Toast.makeText(this, "Please enter a note", Toast.LENGTH_SHORT).show()
            }
        }

        // Display Note Button (Read operation)
        binding.displayNoteButton.setOnClickListener {
            val storedNote = sharedPreferences.getString(noteKey, "")
            if (!storedNote.isNullOrEmpty()) {
                binding.noteTextView.text = storedNote
            } else {
                binding.noteTextView.text = "No note saved"
            }
        }

        // Update Note Button (Edit operation)
        binding.updateNoteButton.setOnClickListener {
            val newNote = binding.notesEditText.text.toString()
            if (newNote.isNotEmpty()) {
                sharedPreferences.edit().putString(noteKey, newNote).apply()
                Toast.makeText(this, "Note Updated Successfully", Toast.LENGTH_SHORT).show()
                binding.notesEditText.text?.clear()

                // Update the widget after updating the note
                updateWidget()
            } else {
                Toast.makeText(this, "Please enter a new note", Toast.LENGTH_SHORT).show()
            }
        }

        // Delete Note Button (Delete operation)
        binding.deleteNoteButton.setOnClickListener {
            sharedPreferences.edit().remove(noteKey).apply()
            binding.noteTextView.text = "Note Deleted"
            Toast.makeText(this, "Note Deleted Successfully", Toast.LENGTH_SHORT).show()

            // Update the widget after deleting the note
            updateWidget()
        }
    }

    // Function to update the widget
    private fun updateWidget() {
        val intent = Intent(this, NoteWidgetProvider::class.java)
        intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        val ids = AppWidgetManager.getInstance(application).getAppWidgetIds(ComponentName(application, NoteWidgetProvider::class.java))
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
        sendBroadcast(intent)
    }
}
