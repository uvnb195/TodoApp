@file:OptIn(ExperimentalMaterial3Api::class)

package com.uvnb195.todoapp.ui.edit_add_todo

import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.uvnb195.todoapp.R
import com.uvnb195.todoapp.util.UiEvent
import java.time.format.DateTimeFormatter

@Composable
fun EditAddScreen(
    viewModel: EditAddViewModel = hiltViewModel(),
    onPopBackStack: () -> Unit
) {
    val localFormat = DateTimeFormatter.ofPattern("dd MMMM yyyy")

    var offSetY by remember {
        mutableIntStateOf(-200)
    }
    val animatedOffset = animateIntOffsetAsState(
        targetValue = IntOffset(0, offSetY),
        animationSpec = tween(
            500,
            easing = LinearOutSlowInEasing
        ),
        label = ""
    )

    val snackBarState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        offSetY = 0
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.PopBackStack -> {
                    onPopBackStack()
                }

                is UiEvent.ShowSnackBar -> {
                    val result = snackBarState.showSnackbar(
                        message = it.message,
                        duration = SnackbarDuration.Short
                    )
                }

                else -> Unit
            }
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackBarState)
        },
        floatingActionButton = {
            IconButton(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        if (viewModel.title.isNotBlank()) MaterialTheme.colorScheme.primary
                        else Color.LightGray
                    ),
                enabled = viewModel.title.isNotBlank(),
                onClick = { viewModel.onEvent(EditAddEvent.OnSaveTodo) }) {
                Icon(
                    modifier = Modifier.size(32.dp),
                    painter = painterResource(id = R.drawable.check_alt),
                    contentDescription = "Save",
                    tint = Color.White
                )
            }
        }
    ) {
        Spacer(modifier = Modifier.height(it.calculateTopPadding()))
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = viewModel.screenTitle,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset { animatedOffset.value },
                    textAlign = TextAlign.Center
                )

                TextField(
                    value = viewModel.title,
                    onValueChange = {
                        viewModel.onEvent(EditAddEvent.OnTitleChanged(it))
                    },
                    placeholder = {
                        Text(
                            text = "Title...",
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.titleLarge,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent
                    )
                )
                TextField(
                    value = viewModel.desc,
                    onValueChange = {
                        viewModel.onEvent(EditAddEvent.OnDescChanged(it))
                    },
                    placeholder = {
                        Text(
                            text = "Description...",
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    leadingIcon = {
                        IconButton(onClick = { /* todo nothing here */ }) {
                            Icon(
                                imageVector = Icons.Outlined.EditNote,
                                contentDescription = "Description",
                                modifier = Modifier.size(40.dp),
                            )
                        }
                    },
                    singleLine = false,
                    maxLines = 5
                )

                val dateState = rememberUseCaseState()
                TextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = viewModel.date.format(localFormat),
                    onValueChange = {},
                    textStyle = MaterialTheme.typography.bodyLarge,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        unfocusedTextColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledTextColor = MaterialTheme.colorScheme.primary,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onBackground,
                        disabledContainerColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent
                    ),
                    enabled = false,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = "Calendar",
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { dateState.show() },
                        )
                    }
                )

                CalendarDialog(
                    state = dateState,
                    config = CalendarConfig(
                        monthSelection = true,
                        yearSelection = true
                    ),
                    selection = CalendarSelection.Date(
                        selectedDate = viewModel.date
                    ) { newDate ->
                        viewModel.onEvent(EditAddEvent.OnDateChanged(newDate))
                    }
                )
            }
            IconButton(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .size(64.dp)
                    .offset { animatedOffset.value },
                onClick = { viewModel.onEvent(EditAddEvent.OnCanceled) }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.x),
                    contentDescription = "Close",
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }

}