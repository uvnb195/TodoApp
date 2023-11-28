package com.uvnb195.todoapp.data

import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    suspend fun deleteDoneTodo()
    suspend fun upsertTodo(todo: Todo)
    suspend fun getTodoById(id: Int): Todo?
    fun getTodosDone(): Flow<List<Todo>>
    fun getAll(): Flow<List<Todo>>
}