package com.uvnb195.todoapp.ui.main_todo_list

import com.uvnb195.todoapp.data.Todo

sealed class TodoListEvent {
    object OnDeletedDoneTodo : TodoListEvent()
    object OnAddedNewTodo : TodoListEvent()
    data class OnTodoClicked(val todo: Todo) : TodoListEvent()
    data class OnDoneChanged(val todo: Todo, val isDone: Boolean) : TodoListEvent()
}