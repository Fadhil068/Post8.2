package com.fadhil_068.post82

interface TaskItemListener{
    fun onEditClick(task: Task)
    fun onDeleteClick(task: Task)
    fun onStatusChanged(task: Task, isCompleted: Boolean)
}

