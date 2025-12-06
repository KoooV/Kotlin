package com.example.module3_3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import com.example.module3_3.presentation.todo.TodoDetailScreen
import com.example.module3_3.presentation.todo.TodoListScreen
import com.example.module3_3.presentation.todo.TodoViewModel
import com.example.module3_3.ui.theme.Module3_3Theme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    private val viewModel: TodoViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return TodoViewModel(this@MainActivity) as T
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module3_3Theme {
                AppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AppContent(viewModel: TodoViewModel) {
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(navController = navController, startDestination = "list", modifier = Modifier.padding(innerPadding)) {
            composable("list") {
                TodoListScreen(viewModel = viewModel, onOpenDetail = { id ->
                    navController.navigate("detail/$id")
                })
            }
            composable("detail/{id}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull() ?: -1
                TodoDetailScreen(viewModel = viewModel, todoId = id, onBack = { navController.popBackStack() })
            }
        }
    }
}
