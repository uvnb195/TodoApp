package com.uvnb195.todoapp.data

import kotlinx.coroutines.flow.Flow

class TodoRepositoryImpl(
    private val dao: Dao
) : TodoRepository {
    override suspend fun deleteTodo(todo: Todo) {
        dao.deleteTodo(todo)
    }

    override suspend fun upsertTodo(todo: Todo) {
        dao.upsertTodo(todo)
    }

    override suspend fun getTodoById(id: Int): Todo? {
        return dao.getTodoById(id)
    }

    override fun getTodosDone(): Flow<List<Todo>> {
        return dao.getTodosDone()
    }

    override fun getAll(): Flow<List<Todo>> {
        return dao.getAll()
    }
}