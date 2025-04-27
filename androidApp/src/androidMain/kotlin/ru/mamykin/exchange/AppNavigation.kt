package ru.mamykin.exchange

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.Flow
import ru.mamykin.exchange.presentation.AppInfoScreen
import ru.mamykin.exchange.presentation.ConverterScreen
import ru.mamykin.exchange.presentation.ConverterScreenEffect
import ru.mamykin.exchange.presentation.ConverterScreenIntent
import ru.mamykin.exchange.presentation.ConverterScreenState

@Composable
// fun AppNavigation(onNightThemeSwitch: (Boolean) -> Unit) {
fun AppNavigation(
    state: ConverterScreenState,
    onIntent: (ConverterScreenIntent) -> Unit,
    effectFlow: Flow<ConverterScreenEffect>,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreen.Main.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable(
            route = AppScreen.Main.route,

            ) {
            ConverterScreen(
                navController = navController,
                state = state,
                onIntent = onIntent,
                effectFlow = effectFlow,
            )
        }

        composable(route = AppScreen.AppInfo.route) {
            AppInfoScreen(navController)
        }
    }
}