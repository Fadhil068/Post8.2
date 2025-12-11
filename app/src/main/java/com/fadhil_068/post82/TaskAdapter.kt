package com.fadhil_068.post82

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fadhil_068.post82.databinding.ItemTaskBinding


class TaskAdapter(
    private val tasks: List<Task>,
    private val listener: TaskItemListener
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        val binding = holder.binding

        binding.tvTitle.text = task.title
        binding.tvDescription.text = task.description
        binding.tvDeadline.text = task.deadline

        binding.cbComplete.setOnCheckedChangeListener(null)
        binding.cbComplete.isChecked = task.isCompleted

        if (task.isCompleted) {

            binding.tvTitle.paintFlags = binding.tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG

            binding.tvTitle.setTextColor(Color.GRAY)
            binding.tvDescription.setTextColor(Color.parseColor("#B0B0B0"))
            binding.tvDeadline.setTextColor(Color.parseColor("#B0B0B0"))

            binding.root.alpha = 0.5f
        } else {
            binding.tvTitle.paintFlags = binding.tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()

            binding.tvTitle.setTextColor(Color.parseColor("#1C1B1F"))
            binding.tvDescription.setTextColor(Color.parseColor("#49454F"))
            binding.tvDeadline.setTextColor(Color.parseColor("#49454F"))

            binding.root.alpha = 1.0f
        }

        binding.cbComplete.setOnCheckedChangeListener { _, isChecked ->
            listener.onStatusChanged(task, isChecked)
        }

        binding.btnDelete.setOnClickListener {
            listener.onDeleteClick(task)
        }

        binding.root.setOnClickListener {
            if (!task.isCompleted) {
                listener.onEditClick(task)
            }
        }
    }

    override fun getItemCount() = tasks.size
}