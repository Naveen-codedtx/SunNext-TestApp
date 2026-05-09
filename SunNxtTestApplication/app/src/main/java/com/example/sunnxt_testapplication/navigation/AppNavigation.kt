package com.example.sunnxt_testapplication.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sunnxt_testapplication.core.device.DeviceType
import com.example.sunnxt_testapplication.ui.home.HomeScreen
import com.example.sunnxt_testapplication.ui.language.LanguageSelectionScreen

object Routes {
    const val LANGUAGE_SELECTION = "language_selection"
    const val HOME = "home"
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    deviceType: DeviceType,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LANGUAGE_SELECTION,
        modifier = modifier,
    ) {
        composable(Routes.LANGUAGE_SELECTION) {
            LanguageSelectionScreen(
                deviceType = deviceType,
                onLanguagesSelected = { selectedIds ->
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LANGUAGE_SELECTION) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.HOME) {
            HomeScreen(deviceType = deviceType)
        }
    }
}
