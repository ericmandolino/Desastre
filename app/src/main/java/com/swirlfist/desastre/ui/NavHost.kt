package com.swirlfist.desastre.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.swirlfist.desastre.ui.view.AddOrEditReminderScreen
import com.swirlfist.desastre.ui.view.TodoMainScreen

@Composable
fun NavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "main",
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(
            route = "main",
        ) {
            TodoMainScreen(
                onNavigateToAddReminder = { todoId ->
                    navController.navigate("addReminder/$todoId")
                },
                onNavigateToEditReminder = { todoId, reminderId ->
                    navController.navigate("editReminder/$todoId/$reminderId")
                }
            )
        }
        composable(
            route = "addReminder/{todoId}",
            arguments = listOf(navArgument("todoId") { type = NavType.LongType }),
            ) {navBackStackEntry ->
            AddOrEditReminderScreen(
                onReminderCompleted = { popBack(navController) },
                todoId = navBackStackEntry.arguments?.getLong("todoId"),
            )
        }
        composable(
            route = "editReminder/{todoId}/{reminderId}",
            arguments = listOf(
                navArgument("todoId") { type = NavType.LongType },
                navArgument("reminderId") { type = NavType.LongType },
            ),
        ) {navBackStackEntry ->
            AddOrEditReminderScreen(
                onReminderCompleted = { popBack(navController) },
                todoId = navBackStackEntry.arguments?.getLong("todoId"),
                reminderId = navBackStackEntry.arguments?.getLong("reminderId"),
            )
        }
    }
}

private fun popBack(
    navController: NavHostController,
) {
    val navigated = navController.popBackStack()
    if (!navigated) {
        navController.navigate("main")
    }
}