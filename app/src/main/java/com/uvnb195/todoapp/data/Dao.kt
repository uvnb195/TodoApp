package com.uvnb195.todoapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Delete
    suspend fun deleteTodo(todo: Todo)

    @Upsert
    suspend fun upsertTodo(todo: Todo)

    @Query("SELECT * FROM Todo WHERE id= :id")
    suspend fun getTodoById(id: Int): Todo?

    @Query("SELECT * FROM Todo WHERE isDone= 1")
    fun getTodosDone(): Flow<List<Todo>>

    @Query("SELECT * FROM Todo ORDER BY date DESC")
    fun getAll(): Flow<List<Todo>>
}