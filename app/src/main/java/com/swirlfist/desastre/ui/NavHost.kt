package com.swirlfist.desastre.ui

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.swirlfist.desastre.ui.view.AddOrEditReminderScreen
import com.swirlfist.desastre.ui.view.TodoMainScreen
import com.swirlfist.desastre.ui.viewmodel.AddOrEditReminderScreenViewModel
import com.swirlfist.desastre.ui.viewmodel.TodosMainScreenViewModel

@Composable
fun NavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "main",
    mainScreenViewModel: TodosMainScreenViewModel = hiltViewModel(),
    addOrEditReminderScreenViewModel: AddOrEditReminderScreenViewModel = hiltViewModel(),
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(
            route = "main",
        ) {
            TodoMainScreen(
                todosMainScreenViewModel = mainScreenViewModel,
                onNavigateToAddReminder = {
                    navController.navigate("addReminder/$it")
                }
            )
        }
        composable(
            route = "addReminder/{todoId}",
            arguments = listOf(navArgument("todoId") { type = NavType.LongType }),
            ) {navBackStackEntry ->
            AddOrEditReminderScreen(
                addOrEditReminderScreenViewModel,
                todoId = navBackStackEntry.arguments?.getLong("todoId"),
                reminderId = null,
            )
        }
    }
}