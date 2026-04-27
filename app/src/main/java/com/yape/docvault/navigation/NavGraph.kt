package com.yape.docvault.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yape.detail_presentation.ui.DocumentDetailScreen
import com.yape.home_presentation.ui.HomeScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "list") {
        composable("list") {
            HomeScreen(
                onNavigateToDetail = { id -> navController.navigate("detail/$id") },
                viewModel = koinViewModel()
            )
        }
        composable(
            route = "detail/{documentId}",
            arguments = listOf(navArgument("documentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: return@composable
            DocumentDetailScreen(
                onNavigateBack = { navController.navigateUp() },
                viewModel = koinViewModel(parameters = { parametersOf(documentId) })
            )
        }
    }
}
