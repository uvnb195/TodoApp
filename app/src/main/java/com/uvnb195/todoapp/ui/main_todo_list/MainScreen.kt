@file:OptIn(ExperimentalFoundationApi::class)

package com.uvnb195.todoapp.ui.main_todo_list

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.uvnb195.todoapp.R
import com.uvnb195.todoapp.data.Todo
import com.uvnb195.todoapp.util.Routes
import com.uvnb195.todoapp.util.UiEvent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TodoItem(
    color: Color,
    todo: Todo,
    modifier: Modifier = Modifier,
    onEvent: (TodoListEvent) -> Unit,
    animFromHorizontal: String,
    index: Int
) {
    val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
    var visible by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(Unit) {
        delay((index + 1) * 100L)
        visible = true
    }
    AnimatedVisibility(visible = visible,
        enter = fadeIn() + slideInHorizontally(
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            )
        ) {
            when (animFromHorizontal) {
                "left" -> -it
                else -> it * 2
            }
        }) {
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
                        checkedColor = color,
                        uncheckedColor = color
                    )
                )
                Text(
                    text = todo.title.uppercase(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    textDecoration = if (color == MaterialTheme.colorScheme.primary) TextDecoration.None else TextDecoration.LineThrough
                )
                Text(
                    text = formatter.format(todo.date),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Divider(
                color = color,
                modifier = modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun PageItem(
    modifier: Modifier = Modifier,
    title: String,
    viewModel: MainViewModel = hiltViewModel()
) {
    val todos: State<List<Todo>>
    val mainColor: Color
    when (title) {
        "My Task" -> {
            todos = viewModel.todos.collectAsState(initial = emptyList())
            mainColor = MaterialTheme.colorScheme.primary
        }

        else -> {
            todos = viewModel.doneTodos.collectAsState(initial = emptyList())
            mainColor = MaterialTheme.colorScheme.secondary
        }
    }

    Column(modifier = modifier) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
        ) {
            val (text, noti) = createRefs()
            var offsetY by remember {
                mutableFloatStateOf(-200f)
            }

            val animatedOffsetY = animateFloatAsState(
                targetValue = offsetY,
                animationSpec = tween(
                    300,
                    easing = LinearOutSlowInEasing
                ),
                label = "title_anim"
            )

            LaunchedEffect(key1 = Unit) {
                offsetY = 0f
            }

            Text(
                modifier = Modifier
                    .constrainAs(text) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .offset { IntOffset(0, animatedOffsetY.value.toInt()) },
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = mainColor
            )
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    .constrainAs(noti) {
                        start.linkTo(text.end, margin = 4.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)

                    }
                    .offset { IntOffset(0, animatedOffsetY.value.toInt()) }
            ) {
                Text(
                    text = todos.value.size.toString(),
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.surface
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .weight(1f)
        ) {
            itemsIndexed(todos.value) { index, todo ->
                TodoItem(
                    color = mainColor,
                    todo = todo,
                    onEvent = viewModel::onEvent,
                    modifier = Modifier
                        .shadow(4.dp)
                        .height(50.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable {
                            viewModel.onEvent(TodoListEvent.OnTodoClicked(todo))
                        },
                    animFromHorizontal = when (title) {
                        "My Task" -> "left"
                        else -> "right"
                    },
                    index = index
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

}

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = hiltViewModel(),
    onNavigate: (UiEvent.Navigate) -> Unit
) {
    var title: String

    val scope = rememberCoroutineScope()

    val snackBarState = remember { SnackbarHostState() }

    val pagerState = rememberPagerState { 2 }

    val animatedColor =
        animateColorAsState(
            targetValue = if (pagerState.currentPage == 0) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.secondary,
            animationSpec = tween(
                300
            ),
            label = ""
        )

    var offSetY by remember {
        mutableIntStateOf(200)
    }
    val animatedOffset = animateIntOffsetAsState(
        targetValue = IntOffset(0, offSetY),
        animationSpec = tween(
            500,
            easing = LinearOutSlowInEasing
        ),
        label = ""
    )

    LaunchedEffect(key1 = pagerState.currentPage) {
        delay(300)
        offSetY = 0
    }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect {
            when (it) {
                is UiEvent.Navigate -> {
                    onNavigate(it)
                }

                is UiEvent.ShowSnackBar -> {
                    snackBarState.showSnackbar(
                        message = it.message,
                        actionLabel = it.action,
                        duration = SnackbarDuration.Short
                    )
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
        Box(modifier = Modifier.fillMaxSize()) {

            Column(modifier = Modifier.padding(top = it.calculateTopPadding())) {
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
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.align(Alignment.BottomCenter),
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        offSetY = 200
                                        pagerState.animateScrollToPage(0)
                                    }
                                },
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .size(48.dp)
                                    .align(Alignment.Center)
                                    .background(
                                        if (pagerState.currentPage == 0) MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.3f
                                        ) else Color.Transparent
                                    ),
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.home),
                                    contentDescription = "Home",
                                    Modifier.size(40.dp)
                                )
                            }
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        offSetY = 200
                                        pagerState.animateScrollToPage(1)
                                    }
                                },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .align(Alignment.Center)
                                    .background(
                                        if (pagerState.currentPage == 1) MaterialTheme.colorScheme.primary.copy(
                                            alpha = 0.3f
                                        ) else Color.Transparent
                                    ),
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.list),
                                    contentDescription = "Completed",
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                }
            }
            IconButton(
                modifier = Modifier
                    .offset { animatedOffset.value }
                    .padding(bottom = 24.dp)
                    .shadow(4.dp, shape = CircleShape)
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(
                        animatedColor.value
                    )
                    .align(Alignment.BottomCenter),
                onClick = {
                    if (pagerState.currentPage == 0)
                        viewModel.sendUiEvent(UiEvent.Navigate(Routes.EDIT_ADD_TODO))
                    else {
                        viewModel.onEvent(TodoListEvent.OnDeletedDoneTodo)
                    }
                }) {
                Log.d("button", "recompose")
                Icon(
                    painterResource(id = if (pagerState.currentPage == 0) R.drawable.plus else R.drawable.trash),
                    contentDescription = if (pagerState.currentPage == 0) "Add" else "Remove All",
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )

            }
//            IconButton(
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(top = 8.dp, end = 8.dp)
//                    .size(24.dp),
//                onClick = { /*TODO*/ }) {
//                Icon(
//                    painter = painterResource(id = R.drawable.moon),
//                    contentDescription = "Light Mode"
//                )
//            }
        }
    }


}