package ru.mamykin.exchange

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.core.component.KoinComponent
import org.koin.java.KoinJavaComponent.inject
import ru.mamykin.exchange.presentation.AppInfoScreen
import ru.mamykin.exchange.presentation.ConverterScreen
import ru.mamykin.exchange.presentation.ConverterViewModel

private object ViewModelHolder : KoinComponent {
    val converterViewModel: ConverterViewModel by inject(ConverterViewModel::class.java)
}

@Composable
// fun AppNavigation(onNightThemeSwitch: (Boolean) -> Unit) {
internal fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreen.Main.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None },
    ) {
        composable(route = AppScreen.Main.route) {
            val viewModel = ViewModelHolder.converterViewModel
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