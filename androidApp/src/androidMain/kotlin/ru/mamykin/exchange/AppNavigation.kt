package ru.mamykin.exchange

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.mamykin.exchange.di.KoinHelper
import ru.mamykin.exchange.presentation.AppInfoScreen
import ru.mamykin.exchange.presentation.ConverterScreen

@Composable
internal fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreen.Main.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable(route = AppScreen.Main.route) {
            val viewModel = KoinHelper.getViewModel()
            val state by viewModel.stateFlow.collectAsState()
            ConverterScreen(
                navController = navController,
                state = state,
                effectFlow = viewModel.effectFlow,
                onIntent = viewModel::onIntent,
            )
        }

        composable(route = AppScreen.AppInfo.route) {
            AppInfoScreen(navController)
        }
    }
}