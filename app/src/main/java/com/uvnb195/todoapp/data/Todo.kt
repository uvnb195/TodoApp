package com.uvnb195.todoapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val title: String,
    val description: String?,
    val isDone: Boolean = false,
    val date: Date
)