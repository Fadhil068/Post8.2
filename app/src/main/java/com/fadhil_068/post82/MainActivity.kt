package com.fadhil_068.post82

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.fadhil_068.post82.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(), TaskItemListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var tasksRef: DatabaseReference
    private lateinit var adapter: TaskAdapter
    private val taskList = mutableListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tasksRef = FirebaseDatabase.getInstance().getReference("tasks")

        setupRecyclerView()
        toggleEmptyState()
        fetchData()

        binding.fabAddTask.setOnClickListener {
            TaskDialog(this, tasksRef).show()
        }
    }

    private fun setupRecyclerView() {
        adapter = TaskAdapter(taskList, this)
        binding.rvTasks.layoutManager = LinearLayoutManager(this)
        binding.rvTasks.adapter = adapter
    }

    private fun fetchData() {
        tasksRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                taskList.clear()
                for (data in snapshot.children) {
                    val task = data.getValue(Task::class.java)
                    task?.id = data.key
                    task?.let { taskList.add(it) }
                }

                taskList.sortBy { it.isCompleted }

                if (::adapter.isInitialized) {
                    adapter.notifyDataSetChanged()
                }

                toggleEmptyState()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Gagal ambil data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun toggleEmptyState() {
        if (taskList.isEmpty()) {
            binding.rvTasks.visibility = View.GONE
            binding.layoutEmptyState.visibility = View.VISIBLE
        } else {
            binding.rvTasks.visibility = View.VISIBLE
            binding.layoutEmptyState.visibility = View.GONE
        }
    }

    override fun onEditClick(task: Task) {
        TaskDialog(this, tasksRef, task).show()
    }

    override fun onDeleteClick(task: Task) {
        val id = task.id ?: return

        tasksRef.child(id).removeValue()

        Snackbar.make(binding.root, "Skripsi dihapus", Snackbar.LENGTH_LONG)
            .setAction("Batal") {
                tasksRef.child(id).setValue(task)
            }
            .show()
    }

    override fun onStatusChanged(task: Task, isCompleted: Boolean) {
        val id = task.id ?: return
        tasksRef.child(id).child("isCompleted").setValue(isCompleted)
    }

}