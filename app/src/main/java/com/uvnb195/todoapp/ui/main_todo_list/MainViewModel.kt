package com.uvnb195.todoapp.ui.main_todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvnb195.todoapp.data.Todo
import com.uvnb195.todoapp.data.TodoRepository
import com.uvnb195.todoapp.util.Routes
import com.uvnb195.todoapp.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: TodoRepository
) : ViewModel() {

    val todos = repo.getAll()
    val doneTodos = repo.getTodosDone()

    private var cacheDeletedTodo: Todo? = null

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: TodoListEvent) {
        when (event) {
            is TodoListEvent.OnAddedNewTodo -> {
                TODO()
            }

            is TodoListEvent.OnDeletedTodo -> {
                viewModelScope.launch {
                    cacheDeletedTodo = event.todo
                    repo.deleteTodo(event.todo)
                    sendUiEvent(
                        UiEvent.ShowSnackBar(
                            message = "Item was deleted!",
                            action = "Undo"
                        )
                    )
                }
            }

            is TodoListEvent.OnDoneChanged -> {
                viewModelScope.launch {
                    repo.upsertTodo(
                        event.todo.copy(
                            isDone = event.isDone
                        )
                    )
                }
            }

            is TodoListEvent.OnTodoClicked -> {
                sendUiEvent(UiEvent.Navigate(Routes.EDIT_ADD_TODO + "?todoId=${event.todo.id}"))
            }

            is TodoListEvent.OnUndoDeletedTodo -> {
                viewModelScope.launch {
                    cacheDeletedTodo?.let {
                        repo.upsertTodo(it)
                        cacheDeletedTodo = null
                    }
                }
            }
        }
    }

    fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

}