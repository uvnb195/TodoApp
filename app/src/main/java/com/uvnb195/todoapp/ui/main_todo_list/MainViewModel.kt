package com.uvnb195.todoapp.ui.main_todo_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    fun onEvent(event: TodoListEvent) {
        when (event) {
            is TodoListEvent.OnAddedNewTodo -> {
                TODO()
            }

            is TodoListEvent.OnDeletedDoneTodo -> {
                viewModelScope.launch {
                    doneTodos.collect {
                        if (it.isNotEmpty()) {
                            repo.deleteDoneTodo()
                            sendUiEvent(
                                UiEvent.ShowSnackBar(
                                    message = "Items was deleted!"
                                )
                            )
                        }
                    }
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
        }
    }

    fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }

}