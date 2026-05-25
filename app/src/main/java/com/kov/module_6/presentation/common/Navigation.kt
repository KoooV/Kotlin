package com.kov.module_6.presentation.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.kov.module_6.presentation.detail.PhotoDetailScreen
import com.kov.module_6.presentation.detail.PhotoDetailViewModel
import com.kov.module_6.presentation.list.PhotoListScreen
import com.kov.module_6.presentation.list.PhotoListViewModel

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            val viewModel = remember { PhotoListViewModel() }

            PhotoListScreen(
                viewModel = viewModel,
                navigateToDetail = { photoId ->
                    navController.navigate("detail/$photoId")
                }
            )
        }

        composable(
            route = "detail/{photoId}",
            arguments = listOf(
                navArgument("photoId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val photoId = backStackEntry.arguments?.getString("photoId") ?: ""
            val viewModel = remember {
                val savedStateHandle = SavedStateHandle().apply {
                    set("photoId", photoId)
                }
                PhotoDetailViewModel(savedStateHandle)
            }

            PhotoDetailScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

