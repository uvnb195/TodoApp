package com.uvnb195.todoapp.ui.edit_add_todo

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uvnb195.todoapp.data.Todo
import com.uvnb195.todoapp.data.TodoRepository
import com.uvnb195.todoapp.util.DateUtils
import com.uvnb195.todoapp.util.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class EditAddViewModel @Inject constructor(
    private val repo: TodoRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    var screenTitle = "Add New Task"
        private set
    var title by mutableStateOf("")
        private set

    var desc by mutableStateOf("")
        private set

    var date by mutableStateOf(LocalDate.now())
        private set

    var todo by mutableStateOf<Todo?>(null)
        private set

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val todoId = savedStateHandle.get<Int>("todoId")!!
        if (todoId != -1) {
            screenTitle = "Update Task"
            viewModelScope.launch {
                repo.getTodoById(todoId)?.let {
                    todo = it
                    title = it.title
                    desc = it.description ?: ""
                    date = DateUtils.asLocalDate(it.date)
                }
            }
        }
    }

    fun onEvent(event: EditAddEvent) {
        when (event) {
            is EditAddEvent.OnDateChanged -> {
                date = event.date
            }

            is EditAddEvent.OnDescChanged -> {
                desc = event.desc
            }

            is EditAddEvent.OnTitleChanged -> {
                title = event.title
            }

            is EditAddEvent.OnSaveTodo -> {
                if (title.isBlank()) {
                    sendUiEvent(
                        UiEvent.ShowSnackBar(
                            message = "Title can't be empty!"
                        )
                    )
                } else {
                    viewModelScope.launch {
                        repo.upsertTodo(
                            Todo(
                                id = todo?.id,
                                title = title,
                                description = desc,
                                date = DateUtils.asDate(date),
                                isDone = false
                            )
                        )
                    }
                    sendUiEvent(UiEvent.PopBackStack)
                }
            }

            EditAddEvent.OnCanceled -> {
                todo = null
                sendUiEvent(UiEvent.PopBackStack)
            }
        }
    }

    fun sendUiEvent(event: UiEvent) {
        viewModelScope.launch {
            _uiEvent.send(event)
        }
    }
}