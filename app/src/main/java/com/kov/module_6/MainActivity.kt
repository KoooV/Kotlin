package com.kov.module_6

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.kov.module_6.presentation.common.AppNavigation
import com.kov.module_6.ui.theme.Module_6Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Module_6Theme {
                PhotoCatalogApp()
            }
        }
    }
}

@Composable
fun PhotoCatalogApp() {
    val navController = rememberNavController()
    AppNavigation(
        navController = navController
    )
}
