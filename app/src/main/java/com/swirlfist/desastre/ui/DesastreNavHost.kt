package com.swirlfist.desastre.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.swirlfist.desastre.ui.view.AddOrEditReminderScreen
import com.swirlfist.desastre.ui.view.TodoScreen
import com.swirlfist.desastre.ui.view.TodosMainScreen

@Composable
fun DesastreNavHost(
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
            TodosMainScreen(
                onNavigateToTodo = navController::toTodo,
                onNavigateToAddReminder = navController::toAddReminder,
                onNavigateToEditReminder = navController::toEditReminder,
            )
        }
        composable(
            route = "todo/{todoId}",
            arguments = listOf(navArgument("todoId") { type = NavType.LongType }),
        ) { navBackStackEntry ->
            TodoScreen(
                todoId = navBackStackEntry.arguments?.getLong("todoId"),
                onNavigateToAddReminder = navController::toAddReminder,
                onNavigateToEditReminder = navController::toEditReminder,
            )
        }
        composable(
            route = "addReminder/{todoId}",
            arguments = listOf(navArgument("todoId") { type = NavType.LongType }),
        ) { navBackStackEntry ->
            AddOrEditReminderScreen(
                todoId = navBackStackEntry.arguments?.getLong("todoId"),
                onReminderCompleted = navController::popBack,
            )
        }
        composable(
            route = "editReminder/{todoId}/{reminderId}",
            arguments = listOf(
                navArgument("todoId") { type = NavType.LongType },
                navArgument("reminderId") { type = NavType.LongType },
            ),
        ) { navBackStackEntry ->
            AddOrEditReminderScreen(
                todoId = navBackStackEntry.arguments?.getLong("todoId"),
                reminderId = navBackStackEntry.arguments?.getLong("reminderId"),
                onReminderCompleted = navController::popBack,
            )
        }
    }
}

private fun NavHostController.popBack() {
    val navigated = popBackStack()
    if (!navigated) {
        navigate("main")
    }
}
private fun NavHostController.toTodo(todoId: Long) = navigate("todo/$todoId")
private fun NavHostController.toAddReminder(todoId: Long) = navigate("addReminder/$todoId")
private fun NavHostController.toEditReminder(todoId: Long, reminderId: Long) = navigate("editReminder/$todoId/$reminderId")