@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.uvnb195.todoapp.ui.main_todo_list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.uvnb195.todoapp.R
import com.uvnb195.todoapp.data.Todo
import com.uvnb195.todoapp.util.UiEvent
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

@Composable
fun TodoItem(
    todo: Todo,
    modifier: Modifier = Modifier,
    onEvent: (TodoListEvent) -> Unit,
) {
    val formatter = SimpleDateFormat("dd MMM")
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = todo.isDone,
                onCheckedChange = {
                    onEvent(TodoListEvent.OnDoneChanged(todo, it))
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary
                )
            )
            Text(
                text = todo.title.uppercase(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = formatter.format(todo.date),
                style = MaterialTheme.typography.bodySmall
            )
        }
        Divider(
            color = MaterialTheme.colorScheme.primary,
            modifier = modifier
                .fillMaxHeight()
                .width(4.dp)
                .align(Alignment.CenterEnd)
                .padding(vertical = 8.dp)
        )
    }

}

@Composable
fun PageItem(
    modifier: Modifier = Modifier,
    title: String,
    viewModel: MainViewModel = hiltViewModel()
) {

    val todos = when (title) {
        "My Task" -> viewModel.todos.collectAsState(initial = emptyList())
        else -> viewModel.doneTodos.collectAsState(initial = emptyList())
    }

    Column(modifier = modifier) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (text, noti, mode) = createRefs()

            Text(
                modifier = Modifier.constrainAs(text) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface)
                    .constrainAs(noti) {
                        start.linkTo(text.end, margin = 4.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)

                    }
            ) {
                Text(
                    text = todos.value.size.toString(),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            IconButton(
                modifier = Modifier.constrainAs(mode) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
                onClick = { /*TODO*/ }) {
                Icon(
                    painter = painterResource(id = R.drawable.cloud_moon),
                    contentDescription = "Mode",
                    modifier = Modifier.size(24.dp),
                )
            }
        }
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(todos.value) { todo ->
                TodoItem(todo = todo, onEvent = viewModel::onEvent)
            }
        }
    }

}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier.fillMaxSize(),
    viewModel: MainViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit
) {
    var title: String

    val scope = rememberCoroutineScope()

    val snackBarState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    onNavigate(it)
                }

                is UiEvent.ShowSnackBar -> {
                    val result = snackBarState.showSnackbar(
                        message = it.message,
                        actionLabel = it.action,
                        duration = SnackbarDuration.Short
                    )

                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(TodoListEvent.OnUndoDeletedTodo)
                    }
                }

                else -> Unit
            }
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackBarState)
        }
    ) {
        Column(modifier = Modifier.padding(top = it.calculateTopPadding())) {
            val pagerState = rememberPagerState { 2 }
            HorizontalPager(
                modifier = Modifier.weight(1f),
                state = pagerState
            ) { page ->
                title = if (page % 2 == 0) "My Task" else "Completed"
                PageItem(title = title)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .align(Alignment.Center)
                                .background(
                                    if (pagerState.currentPage == 0) MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.3f
                                    ) else Color.Transparent
                                ),
                        ) {
                            Icon(
                                imageVector =   Icons.Outlined.Home,
                                contentDescription = "Home",
                                modifier = Modifier.fillMaxSize(),
                            )
                        }
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(1)
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .align(Alignment.Center)
                                .background(
                                    if (pagerState.currentPage == 1) MaterialTheme.colorScheme.primary.copy(
                                        alpha = 0.3f
                                    ) else Color.Transparent
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Checklist,
                                contentDescription = "Completed",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        onClick = {
//                            viewModel.sendUiEvent(UiEvent.Navigate(Routes.EDIT_ADD_TODO))
                        }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.size(28.dp)
                        )

                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

        }
    }


}