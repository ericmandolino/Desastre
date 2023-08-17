package com.swirlfist.desastre.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swirlfist.desastre.ui.view.TodoMainScreen
import com.swirlfist.desastre.ui.viewmodel.TodosMainScreenViewModel

@Composable
fun NavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "main",
    mainScreenViewModel: TodosMainScreenViewModel = hiltViewModel(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(
            route = "main"
        ) {
            TodoMainScreen(
                todosMainScreenViewModel = mainScreenViewModel,
            )
        }
    }
}