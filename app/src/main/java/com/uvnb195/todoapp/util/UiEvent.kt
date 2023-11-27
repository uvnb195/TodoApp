package com.uvnb195.todoapp.util

sealed class UiEvent {
    data class Navigate(val route: String) : UiEvent()
    data class ShowSnackBar(
        val message: String,
        val action: String? = null
    ) : UiEvent()

    object PopBackStack : UiEvent()
}
