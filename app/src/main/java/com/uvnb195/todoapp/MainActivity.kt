package com.uvnb195.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.uvnb195.todoapp.ui.edit_add_todo.EditAddScreen
import com.uvnb195.todoapp.ui.main_todo_list.MainScreen
import com.uvnb195.todoapp.ui.theme.TodoAppTheme
import com.uvnb195.todoapp.util.Routes
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor

        setContent {
            TodoAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Routes.MAIN_TODO_LIST
                    ) {
                        composable(Routes.MAIN_TODO_LIST) {
                            MainScreen(
                                modifier = Modifier.fillMaxSize(),
                                onNavigate = { navController.navigate(it.route) })
                        }

                        composable(Routes.EDIT_ADD_TODO + "?todoId={todoId}",
                            arguments = listOf(
                                navArgument("todoId") {
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )) {
                            EditAddScreen {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}