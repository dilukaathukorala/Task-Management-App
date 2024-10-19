package com.example.taskapp1

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class TaskList : AppCompatActivity() {
    private lateinit var taskAdapter: ArrayAdapter<String>
    private val tasks = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)
        val taskInput = findViewById<EditText>(R.id.taskInput)
        val addTaskButton = findViewById<Button>(R.id.addTaskButton)
        val taskListView = findViewById<ListView>(R.id.taskListView)
        val backButton = findViewById<ImageButton>(R.id.backButton) // Back button reference


        taskAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, tasks)
        taskListView.adapter = taskAdapter

        addTaskButton.setOnClickListener {
            val task = taskInput.text.toString()
            if (task.isNotEmpty()) {
                tasks.add(task)
                taskAdapter.notifyDataSetChanged()
                taskInput.text.clear()
            }
        }

        taskListView.setOnItemClickListener { _, _, position, _ ->
            tasks.removeAt(position)
            taskAdapter.notifyDataSetChanged()
        }
        // Set back button click listener
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java) // Replace HomeActivity with your actual home activity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK) // Clear the activity stack
            startActivity(intent)
            finish() // Optional: Close current activity
        }

    }
}
