package com.uvnb195.todoapp.ui.edit_add_todo

import java.time.LocalDate
import java.util.Date

sealed class EditAddEvent {
    data class OnTitleChanged(val title: String) : EditAddEvent()
    data class OnDescChanged(val desc: String) : EditAddEvent()
    data class OnDateChanged(val date: LocalDate) : EditAddEvent()
    object OnSaveTodo : EditAddEvent()
    object OnCanceled : EditAddEvent()
}
