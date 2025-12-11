package com.fadhil_068.post82

import android.app.DatePickerDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.fadhil_068.post82.databinding.DialogTaskBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.database.DatabaseReference
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TaskDialog(
    private val context: Context,
    private val tasksRef: DatabaseReference,
    private val taskToEdit: Task? = null
) {
    private lateinit var binding: DialogTaskBinding

    fun show() {
        binding = DialogTaskBinding.inflate(LayoutInflater.from(context))

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(binding.root)

        if (taskToEdit != null) {
            binding.tvDialogTitle.text = "Edit Skripsi"
            binding.etTitle.setText(taskToEdit.title)
            binding.etDescription.setText(taskToEdit.description)
            binding.etDeadline.setText(taskToEdit.deadline)
        } else {
            binding.tvDialogTitle.text = "Tambah Skripsi Baru"
        }

        binding.etDeadline.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                context,
                { _, selectedYear, selectedMonth, selectedDay ->
                    val selectedDate = Calendar.getInstance()
                    selectedDate.set(selectedYear, selectedMonth, selectedDay)

                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    binding.etDeadline.setText(dateFormat.format(selectedDate.time))
                },
                year, month, day
            )
            datePickerDialog.datePicker.minDate = System.currentTimeMillis()

            datePickerDialog.show()
        }

        binding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }


        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()
            val deadline = binding.etDeadline.text.toString().trim()

            if (title.isEmpty()) {
                binding.etTitle.error = "Judul tugas harus diisi!"
                return@setOnClickListener
            }

            if (taskToEdit == null) {
                val newId = tasksRef.push().key ?: return@setOnClickListener
                val newTask = Task(
                    id = newId,
                    title = title,
                    description = description,
                    deadline = deadline,
                    isCompleted = false
                )

                tasksRef.child(newId).setValue(newTask)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Skripsi berhasil ditambah", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                dialog.dismiss()
            } else {
                val taskId = taskToEdit.id ?: return@setOnClickListener
                val updatedTask = Task(
                    id = taskId,
                    title = title,
                    description = description,
                    deadline = deadline,
                    isCompleted = taskToEdit.isCompleted
                )

                tasksRef.child(taskId).setValue(updatedTask)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Skripsi berhasil diperbarui", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

                dialog.dismiss()
            }
        }

        dialog.show()
    }
}
